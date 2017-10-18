(ns clova.core
  "The core namespace contains all of the built in validators and the
  defvalidator macro.

  The only other public functions are

  - `validation-set`
  - `validate`
  - `valid?`
  - `as-validator`

  You can view more information in the [readme] (https://github.com/markwoodhall/clova/blob/master/README.md).
  There are also example validation scenarios [here](http://markwoodhall.github.io/clova/EXAMPLES.html).

  The API documentation is available [here](http://markwoodhall.github.io/clova/).

  You can also view [blog posts] (http://markw.xyz/tags/clova/) about clova.
  "
  {:author "Mark Woodhall"}
  (:require [clova.util :as u]
            [clojure.string :refer [join] :as st]
             #?(:cljs [goog.string :as gstr])
             #?(:cljs [goog.string.format])
             #?(:clj  [clj-time.format :as f])
             #?(:clj  [clj-time.core :as c])
             #?(:cljs [cljs-time.format :as f])
             #?(:cljs [cljs-time.core :as c]))
  #?(:cljs (:require-macros [clova.core :refer [defvalidator]])))

(defmacro defvalidator
  "Wraps body in a function and defines it with meta data
  used to support the validation process.
  Works using a similar pattern to `defn`."
  [doc-string fname validator-meta-data args & body]
  `(do
     (def
       ~(with-meta fname {:doc doc-string :added (:added validator-meta-data) :arglists `'(~args)})
       (with-meta (fn ~fname ([~@args]
                              ~@body))
                  ~validator-meta-data))))

(defvalidator
  "Checks for the presence of a non nil value."
  not-nil?
  {::type :not-nil ::default-message "%s is required." :added "0.2.0" ::allow-missing-key? true}
  [value]
  (u/not-nil? value))

(defvalidator
  "Checks for the presence of a key based on the default value of `::key-not-found?`
  for a missing key."
  required?
  {::type :required ::default-message "%s is required." :added "0.8.0" ::allow-missing-key? false}
  [value]
  (u/not-missing? value))

(defvalidator
  "Checks a string representation of value against regex and
  returns true if value matches the regex. If value is not a
  match then returns nil."
  matches?
  {::type :matches ::default-message "%s is invalid value %s." :added "0.2.0" ::allow-missing-key? true}
  [value regex]
  (when (re-seq regex (str value))
    true))

(defvalidator
  "Checks an input value to see if it is a valid email address"
  email?
  {::type :email ::default-message "%s should be a valid email address." :added "0.2.0" ::allow-missing-key? true}
  [value]
  (matches? value #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$"))

(defvalidator
  "Checks an input value to see if it is a valid zip code."
  zip-code?
  {::type :zip-code ::default-message "%s should be a valid zip code." :added "0.2.0" ::allow-missing-key? true}
  [value]
  (matches? value #"^[0-9]{5}(-[0-9]{4})?$"))

(defvalidator
  "Checks an input value to see if it is a valid uk post code."
  post-code?
  {::type :post-code ::default-message "%s should be a valid post code." :added "0.2.0" ::allow-missing-key? true}
  [value]
  (matches? value #"(?i)^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$"))

(defvalidator
  "Checks an input value to see if it is a valid url."
  url?
  {::type :url ::default-message "%s should be a valid url." :added "0.2.0" ::allow-missing-key? true}
  [value]
  (matches? value #"^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))

(defvalidator
  "Checks an input value to see if it is greater than lower."
  greater?
  {::type :greater ::default-message "%s is %s but it must be greater than %s." :added "0.2.0" ::allow-missing-key? true}
  [value lower]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? lower))
    (> value lower)))

(defvalidator
  "Checks an input value to see if it is less than lower."
  lesser?
  {::type :lesser ::default-message "%s is %s but it must be less than %s." :added "0.2.0" ::allow-missing-key? true}
  [value lower]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? lower))
    (< value lower)))

(defvalidator
  "Checks an input value to see if it is a positive number."
  positive?
  {::type :positive ::default-message "%s is %s but it should be a positive number." :added "0.4.0" ::allow-missing-key? true}
  [value]
  (when (u/not-nil-or-missing? value)
    (pos? value)))

(defvalidator
  "Checks an input value to see if it is a negative number."
  negative?
  {::type :negative ::default-message "%s is %s but it should be a negative number." :added "0.4.0" ::allow-missing-key? true}
  [value]
  (when (u/not-nil-or-missing? value)
    (neg? value)))

(defvalidator
  "Checks an input value to see if it is between lower and upper."
  between?
  {::type :between ::default-message "%s is %s but it must be between %s and %s." :added "0.2.0" ::allow-missing-key? true}
  [value lower upper]
  (when (and (u/not-nil-or-missing? value)
             (not-nil? lower)
             (not-nil? upper))
    (and (>= value lower)
         (<= value upper))))

(defvalidator
  "Check an input value to see if it has a length equal to l.
  Work on sequences and strings."
  length?
  {::type :length ::default-message "%s is %s but it should have a length of %s." :added "0.5.0" ::allow-missing-key? true}
  [value l]
  (when (u/not-nil-or-missing? value)
    (= l (count (seq value)))))

(defvalidator
  "Check an input value to see if it has a length longer than l.
  Work on sequences and strings."
  longer?
  {::type :longer ::default-message "%s is %s but it should have a length longer than %s." :added "0.6.0" ::allow-missing-key? true}
  [value l]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? l))
    (< l (count (seq value)))))

(defvalidator
  "Check an input value to see if it has a length shorter than l.
  Work on sequences and strings."
  shorter?
  {::type :shorter ::default-message "%s is %s but it should have a length shorter than %s." :added "0.6.0" ::allow-missing-key? true}
  [value l]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? l))
    (> l (count (seq value)))))

(defvalidator
  "Checks an input value to see if it is one of the items in a col"
  one-of?
  {::type :one-of ::default-message "%s is %s but should be one of %s." :added "0.2.0" ::allow-missing-key? true}
  [value col]
  (u/not-nil? (some #{value} col)))

(defvalidator
  "Check an input value to see if it matches a given collection
  of predicates. Predicates can be concrete values or they can be functions, they
  can be single items or collections.

  `(all? true [true (fn [v] (= true v))])`
  `(all? true (fn [v] (= true v)))`"
  all?
  {::type :all ::default-message "%s is %s but it does not meet all of the requirements." :added "0.9.0" ::allow-missing-key? true}
  [value col]
  (let [c (u/as-seq col)]
    (every? true? (map #(if (u/function? %)
                          (% value)
                          (if (sequential? %)
                            (let [func (first %)
                                  args (rest %)]
                              (when (u/function? func)
                                (apply func value args)))
                            %)) c))))

(defvalidator
  "Checks an input value to see if it is numeric."
  numeric?
  {::type :numeric ::default-message "%s is %s but it should be a number." :added "0.14.0" ::allow-missing-key? true}
  [value]
  (number? value))

(defvalidator
  "Checks an input value to see if it is a string"
  stringy?
  {::type :stringy ::default-message "%s is %s but it should be a string." :added "0.15.0" ::allow-missing-key? true}
  [value]
  (string? value))

(defvalidator
  "Checks an input value to see if it is alphanumeric. Alphanumeric
  is defined as `[a-zA-Z0-9*$]`, if you need to customise this you can use the
  [[matches?]] validator with a custom regex."
  alphanumeric?
  {::type :alphanumeric ::default-message "%s is %s but it should be an alphanumeric value." :added "0.28.0" ::allow-missing-key? true}
  [value]
  (matches? value #"^[a-zA-Z0-9*$]"))

(defvalidator
  "Check an input value to see if it is equal to v using the regular `=` function.

  It is worthwhile using [[=date?]] for validating date equality, since it has support
  for parsing string dates and comparing DateTime objects."
  =?
  {::type := ::default-message "%s is %s but it should be %s." :added "0.22.0" ::allow-missing-key? true}
  [value v]
  (= value v))

(defvalidator
  "Checks an input value to see if it is a date.

  If value is a string then it is parsed using `clj-time` or `cljs-time`.

  If value is not a string but is one of `[java.util.Date org.joda.time.DateTime]` or
  `[js/Date goog.date.Date goog.date.DateTime]` then it will be considered a `date?`.

  Optionally, takes a map argument and makes use of the following keys:

  - `:formatter` You can use one of the built in ISO8601 formatters
  from clj-time or cljs-time. You can also define your own custom format string."
  date?
  {::type :date ::default-message "%s is %s but it should be a date." :added "0.18.0" ::allow-missing-key? true}
  [value & [opt]]
  (let [{formatter :formatter} opt
        formatter (if (string? formatter)
                    (f/formatter formatter)
                    formatter)
        valid-types #?(:clj [java.util.Date org.joda.time.DateTime]
                       :cljs [js/Date goog.date.Date goog.date.DateTime])]
    (if (some true? (map #(instance? % value) valid-types))
      true
      (try
        (not-nil? (if formatter
                    (f/parse formatter value)
                    (f/parse value)))
        #?(:clj  (catch Exception e false))
        #?(:cljs (catch js/Error e false))))))

(defvalidator
  "Check an input value to see if it is chronoligically equal to d. Where
  d is either the string representation of a date or one of `[java.util.Date org.joda.time.DateTime]` or
  `[js/Date goog.date.Date goog.date.DateTime]`

  Optionally, takes a map argument and makes use of the following keys:

  - `:formatter` You can use one of the built in ISO8601 formatters
  from clj-time or cljs-time. You can also define your own custom format string."
  =date?
  {::type :=date ::default-message "%s is %s but it should be %s." :added "0.21.0" ::allow-missing-key? true}
  [value d & [opt]]
  (if (and (not-nil? value)
           (not-nil? d))
    (let [{formatter :formatter} opt
          value (u/to-clj-date value formatter)
          d (u/to-clj-date d formatter)]
      (c/equal? value d))))

(defvalidator
  "Check an input value to see if it is chronoligically after d. Where
  d is either the string representation of a date or one of `[java.util.Date org.joda.time.DateTime]` or
  `[js/Date goog.date.Date goog.date.DateTime]`

  Optionally, takes a map argument and makes use of the following keys:

  - `:formatter` You can use one of the built in ISO8601 formatters
  from clj-time or cljs-time. You can also define your own custom format string."
  after?
  {::type :after ::default-message "%s is %s but it should be after %s." :added "0.20.0" ::allow-missing-key? true}
  [value d & [opt]]
  (if (and (not-nil? value)
           (not-nil? d))
    (let [{formatter :formatter} opt
          value (u/to-clj-date value formatter)
          d (u/to-clj-date d formatter)]
      (c/after? value d))))

(defvalidator
  "Check an input value to see if it is chronoligically before d. Where
  d is either the string representation of a date or one of `[java.util.Date org.joda.time.DateTime]` or
  `[js/Date goog.date.Date goog.date.DateTime]`

  Optionally, takes a map argument and makes use of the following keys:

  - `:formatter` You can use one of the built in ISO8601 formatters
  from clj-time or cljs-time. You can also define your own custom format string."
  before?
  {::type :before ::default-message "%s is %s but it should be before %s." :added "0.19.0" ::allow-missing-key? true}
  [value d & [opt]]
  (if (and (not-nil? value)
           (not-nil? d))
    (let [{formatter :formatter} opt
          value (u/to-clj-date value formatter)
          d (u/to-clj-date d formatter)]
      (c/before? value d))))

(defvalidator
  "Chacks an input value to see if it is a \"valid\" credit
 card number based on the Luhn algorithm."
  credit-card?
  {::type :credit-card ::default-message "%s is %s but it should be a valid credit card number." :added "0.11.0" ::allow-missing-key? true}
  [value]
  (when (u/not-nil? value)
    (let [value (str value)
          value (st/replace value #" " "")
          value (st/replace value #"-" "")
          factors (flatten (repeat [1 2]))
          numbers (map #?(:clj #(Character/digit % 10)
                          :cljs #(js/parseInt %)) (seq value))
          sum (reduce + (map #(int (+ (/ % 10) (mod % 10)))
                             (map * (reverse numbers) factors)))]
      (zero? (mod sum 10)))))

(defvalidator
  "Checks for the non presence of an item in a collection."
  not-exists?
  {::type :not-not-exists ::default-message "%s already exists." :added "0.31.0" ::allow-missing-key? true}
  [value col]
  (when (u/not-nil? value)
    (not (some #{value} col))))

(defn as-validator
  "Takes a function f and applies optional m as meta data around it. f should be accept
  a first argument as the value to validate.

  When m is present the following keys are taken and used as meta data to declare a validator:

  - `:default-message` The default message template to be used when validation fails.
  - `:allow-missing-key?` Should validation fail if the `:target` key is not present.

  When m is not specified suitable defaults are used."
  ([f]
   (as-validator f {}))
  ([f {:keys [default-message allow-missing-key?]
       :or {default-message "%s is %s but this is not a valid value."
            allow-missing-key? true}
       :as m}]
   (let [m-data {::type :as-validator ::default-message default-message ::allow-missing-key? allow-missing-key?}]
     (with-meta f m-data))))

(defn validation-set
  "Takes a sequence (col) that represents
  keys to validate and the functions used to validate them.

  e.g. `[:email email? :post-code post-code?]`

  It is also possible to specify keys to traverse nested maps.

  e.g. using `[[:user :credentials :name] [matches? #\"someregex\"]]`
  we can define a validation function to target the `:name` key in a map like
  `{:user {:credentials {:name \"username\" }}}`

  Returns a sequence of functions merged with meta data used by
  the validation."
  [col]
  (let [key-or-key-seq? (fn [i] (or (keyword? i)
                                    (and (sequential? i)
                                         (every? keyword? i))))
        key-func-pairs (partition 2 (partition-by key-or-key-seq? col))
        metaify (fn [f target] (let [func-or-seq f
                                     func (if (sequential? func-or-seq)
                                            (first func-or-seq)
                                            func-or-seq)
                                     func-meta (meta func)
                                     args (if (sequential? func-or-seq)
                                            {::args (rest func-or-seq)})
                                     val-meta (merge args {::target target})]
                                 (with-meta func (merge func-meta val-meta))))]
    (flatten (map #(let [target (first (first %))
                         function-seq (second %)]
                     (map (fn [f] (metaify f target)) function-seq)) key-func-pairs))))

(defn validate
  "Takes a validation set and applies it to m.
  Returns a map containing `:valid?` with either a truthy or falsy value and
  `:results` which is a sequence of validation failure messages, if applicable.

  Optionally takes a map of options:

  `:default-message-fn` can be specified to override the default validation messages. If specified
  the function will be called and receive the validator type as an argument. If the result of calling
  the function is anything but nil it will be used as the default validation message.

  `:short-circuit?` when true no further validators for the validation set will be processed.
  The default is false and therefore to process all validators."
  ([v-set m]
   (validate v-set m {}))
  ([v-set m {:keys [default-message-fn short-circuit?]
             :or {default-message-fn (fn [v-type value args] nil)
                  short-circuit? false}}]
   (let [done (atom false)
         valids (map #(when (or (not short-circuit?)
                                (not @done))
                          (let [{v-type ::type target ::target args ::args
                                 allow-missing-key? ::allow-missing-key? default-message ::default-message
                                 :or {default-message "%s has value %s, which is invalid."
                                      v-type :function
                                      allow-missing-key? true}} (meta %)
                                target (u/as-seq target)
                                target-name (join " " (map name target))
                                value (get-in m target ::key-not-found?)
                                realised-args (map (fn [arg] 
                                                     (if (u/function? arg)
                                                       (arg value)
                                                       arg)) args)
                                message (u/func-or-default (partial default-message-fn v-type value realised-args) default-message)
                                valid? (or (and allow-missing-key?
                                                (= ::key-not-found? value))
                                           (apply % value realised-args))]
                            (reset! done (not valid?))
                            {:valid? valid?
                             :message (when-not valid?
                                        #?(:clj (apply format message target-name value realised-args)
                                           :cljs (apply gstr/format message target-name value realised-args)))})) v-set)]
     {:valid? (every? true? (map :valid? valids))
      :results (remove nil? (map :message valids))})))

(defn valid?
  "Takes a validation set and applies it to m.
  This is just a shorthand method over the validate function and returns
  only a truthy or falsy value indicating the validation status."
  [v-set m]
  (:valid? (validate v-set m)))
