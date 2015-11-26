(ns clova.core
  (:require [clova.util :as u]
            [clojure.string :refer [join]]
             #?(:cljs [goog.string :as gstr])
             #?(:cljs [goog.string.format]))
  #?(:cljs (:require-macros [clova.core :refer [defvalidator]])))

(defmacro defvalidator
  "Wraps body in a function and defines it with meta data
  used to support the validation process.
  Works using a similar pattern to \"defn\"."
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
  {:clova.core/type :not-nil :clova.core/default-message "%s is required." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value]
  (u/not-nil? value))

(defvalidator
  "Checks for the presence of a key based on the default value of :clova.core/key-not-found?
  for a missing key."
  required?
  {:clova.core/type :required :clova.core/default-message "%s is required." :added "0.8.0" :clova.core/allow-missing-key? false}
  [value]
  (u/not-missing? value))

(defvalidator
  "Checks a string representation of value against regex and
  returns true if value matches the regex. If value is not a
  match then returns nil."
  matches?
  {:clova.core/type :matches :clova.core/default-message "%s is invalid value %s." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value regex]
  (when (re-seq regex (str value))
    true))

(defvalidator
  "Checks an input value to see if it is a valid email address"
  email?
  {:clova.core/type :email :clova.core/default-message "%s should be a valid email address." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value]
  (matches? value #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$"))

(defvalidator
  "Checks an input value to see if it is a valid zip code."
  zip-code?
  {:clova.core/type :zip-code :clova.core/default-message "%s should be a valid zip code." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value]
  (matches? value #"^[0-9]{5}(-[0-9]{4})?$"))

(defvalidator
  "Checks an input value to see if it is a valid uk post code."
  post-code?
  {:clova.core/type :post-code :clova.core/default-message "%s should be a valid post code." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value]
  (matches? value #"(?i)^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$"))

(defvalidator
  "Checks an input value to see if it is a valid url."
  url?
  {:clova.core/type :url :clova.core/default-message "%s should be a valid url." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value]
  (matches? value #"^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))

(defvalidator
  "Checks an input value to see if it is greater than lower."
  greater?
  {:clova.core/type :greater :clova.core/default-message "%s is %s but it must be greater than %s." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value lower]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? lower))
    (> value lower)))

(defvalidator
  "Checks an input value to see if it is less than lower."
  lesser?
  {:clova.core/type :lesser :clova.core/default-message "%s is %s but it must be less than %s." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value lower]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? lower))
    (< value lower)))

(defvalidator
  "Checks an input value to see if it is a positive number."
  positive?
  {:clova.core/type :positive :clova.core/default-message "%s is %s but it should be a positive number." :added "0.4.0" :clova.core/allow-missing-key? true}
  [value]
  (when (u/not-nil-or-missing? value)
    (pos? value)))

(defvalidator
  "Checks an input value to see if it is a negative number."
  negative?
  {:clova.core/type :negative :clova.core/default-message "%s is %s but it should be a negative number." :added "0.4.0" :clova.core/allow-missing-key? true}
  [value]
  (when (u/not-nil-or-missing? value)
    (neg? value)))

(defvalidator
  "Checks an input value to see if it is between lower and upper."
  between?
  {:clova.core/type :between :clova.core/default-message "%s is %s but it must be between %s and %s." :added "0.2.0" :clova.core/allow-missing-key? true}
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
  {:clova.core/type :length :clova.core/default-message "%s is %s but it should have a length of %s." :added "0.5.0" :clova.core/allow-missing-key? true}
  [value l]
  (when (u/not-nil-or-missing? value)
    (= l (count (seq value)))))

(defvalidator
  "Check an input value to see if it has a length longer than l.
  Work on sequences and strings."
  longer?
  {:clova.core/type :longer :clova.core/default-message "%s is %s but it should have a length longer than %s." :added "0.6.0" :clova.core/allow-missing-key? true}
  [value l]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? l))
    (< l (count (seq value)))))

(defvalidator
  "Check an input value to see if it has a length shorter than l.
  Work on sequences and strings."
  shorter?
  {:clova.core/type :shorter :clova.core/default-message "%s is %s but it should have a length shorter than %s." :added "0.6.0" :clova.core/allow-missing-key? true}
  [value l]
  (when (and (u/not-nil-or-missing? value)
             (u/not-nil? l))
    (> l (count (seq value)))))

(defvalidator
  "Checks an input value to see if its one of the items in a col"
  one-of?
  {:clova.core/type :one-of :clova.core/default-message "%s is %s but should be one of %s." :added "0.2.0" :clova.core/allow-missing-key? true}
  [value col]
  (u/not-nil? (some #{value} col)))

(defvalidator
  "Check an input value to see if it matches a given collection
  of predicates. Predicates can be concrete values or they can be functions, they
  can be single items or collections.

  (all? true [true (fn [v] (= true v))]
  (all? true (fn [v] (= true v)))"
  all?
  {:clova.core/type :all :clova.core/default-message "%s is %s but it does not meet all of the requirements." :added "0.9.0" :clova.core/allow-missing-key? true}
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

(defn validation-set
  "Takes a sequence (col) that represents
  keys to validate and the functions used to validate them.

  e.g. [:email email? :post-code post-code?]

  It is also possible to specify keys to traverse nested maps.

  e.g. using [[:user :credentials :name] [matches? #\"someregex\"]]
  we can define a validation function to target the :name key in a map like
  {:user {:credentials {:name \"username\" }}}

  Returns a sequence of functions merged with meta data used by
  the validation."
  [col]
  {:pre [(even? (count col))]}
  (map #(let [func-or-seq (second %)
              func (if (sequential? func-or-seq)
                     (first func-or-seq)
                     func-or-seq)
              func-meta (meta func)
              args (if (sequential? func-or-seq)
                     {:clova.core/args (rest func-or-seq)})
              val-meta (merge args {:clova.core/target (first %)})]
          (with-meta func (merge func-meta val-meta))) (partition 2 col)))

(defn validate
  "Takes a validation set an applies it to m.
  Returns a map containing :valid? which either has a truthy or falsy value as
  well as a sequence of validation failure messages, if applicable.

  Optionally takes a map of options:

  :default-message-fn can be specified to override the default validation messages. If specified
  the function will be called and receive the validator type as an argument. If the result of calling
  the function is anything but nil it will be used as the default validation message."
  ([v-set m]
   (validate v-set m {}))
  ([v-set m {:keys [default-message-fn]
             :or {default-message-fn (fn [x] nil)}}]
   (let [valids (map #(let [{v-type :clova.core/type target :clova.core/target args :clova.core/args
                             allow-missing-key? :clova.core/allow-missing-key? default-message :clova.core/default-message} (meta %)
                             target (u/as-seq target)
                             target-name (join " " (map name target))
                             value (get-in m target :clova.core/key-not-found?)
                             message (u/func-or-default (partial default-message-fn v-type) default-message)]
                         {:valid? (or (and allow-missing-key?
                                           (= :clova.core/key-not-found? value))
                                      (apply % value args))
                          :message #?(:clj (apply format message target-name value args)
                                      :cljs (apply gstr/format message target-name value args))}) v-set)]
     {:valid? (every? true? (map :valid? valids))
      :results (map :message (filter #(not (:valid? %)) valids))})))

(defn valid?
  "Takes a validation set and applies it to m.
  This is just a shorthand method over the validate function and returns
  only a truthy or falsy value indicating the validation status."
  [v-set m]
  (:valid? (validate v-set m)))
