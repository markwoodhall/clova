(ns clova.core
  #+cljs (:require [goog.string :as gstring]
                   [goog.string.format])
  #+cljs (:require-macros [clova.core :refer [defvalidator]]))

(defmacro defvalidator
  "Wraps body in a function and defines it with meta data
  used to support the validation process.
  Works using a similar pattern to \"defn\"."
  [doc-string fname validator-meta-data args & body]
  `(do
     (def
       ~(with-meta fname {:doc doc-string :arglists `'(~args)})
       (with-meta (fn ~fname ([~@args]
                              ~@body))
                  ~validator-meta-data))))

(defvalidator
  "Checks a string representation of value against regex and
  returns true if value matches the regex. If value is not a
  match then returns nil."
  matches?
  {:type :matches :default-message "%s is an invalid value for %s."}
  [value regex]
  (when (re-seq regex (str value))
    true))

(defvalidator
  "Checks an input value to see if it is a valid email address"
  email?
  {:type :email :default-message "%s is an invalid value for %s."}
  [value]
  (matches? value #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$"))

(defvalidator
  "Checks an input value to see if it is a valid zip code."
  zip-code?
  {:type :zip-code :default-message "%s is an invalid value for %s."}
  [value]
  (matches? value #"^[0-9]{5}(-[0-9]{4})?$"))

(defvalidator
  "Checks an input value to see if it is a valid uk post code."
  post-code?
  {:type :post-code :default-message "%s is an invalid value for %s."}
  [value]
  (matches? value #"(?i)^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$"))

(defvalidator
  "Checks an input value to see if it is a valid url."
  url?
  {:type :url :default-message "%s is an invalid value for %s."}
  [value]
  (matches? value #"^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))

(defvalidator
  "Checks an input value to see if it is between lower and upper."
  between?
  {:type :between :default-message "%s is an invalid value for %s, it must be between %s and %s."}
  [value lower upper]
  (and (>= value lower)
       (<= value upper)))

(defvalidator
  "Checks an input value to see if its one of the items in a col"
  one-of?
  {:type :one-of :default-message "%s is an invalid value for %s."}
  [value col]
  (some #{value} col))

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
                     {:args (rest func-or-seq)})
              val-meta (merge args {:target (first %)})]
          (with-meta func (merge func-meta val-meta))) (partition 2 col)))

(defn validate
  "Takes a validation set an applies it to m.
  Returns a map containing :valid? which either has a truthy or falsy value as
  well as a sequence of validation failure messages, if applicable."
  [v-set m]
  (let [valids (map (fn [v]
                      (let [target (:target (meta v))
                            target (if (not (sequential? target))
                                       [target]
                                       target)
                            target-name (reduce #(str %1 " " %2) (map name target))
                            value (get-in m target)
                            args (:args (meta v))
                            message (:default-message (meta v))]
                        {:valid? (apply v value args)
                         :message #+clj (apply format message value target-name args)
                         #+cljs (apply gstring/format message value target-name args)})) v-set)]
    {:valid? (reduce #(and %1 %2) true (map :valid? valids))
     :results (map :message (filter #(not (:valid? %)) valids))}))

(defn valid?
  "Takes a validation set and applies it to m.
  This is just a shorthand method over the validate function and returns
  only a truthy or falsy value indicating the validation status."
  [v-set m]
  (:valid? (validate v-set m)))
