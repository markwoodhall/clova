(ns clova.core
  #+cljs (:require-macros [clova.core :refer [defvalidator]]))

(defmacro defvalidator
  "Defines a new function that validates its arguments.
  Works using a similar pattern to \"defn\"."
  [doc-string fname validator-meta-data args & body]
  `(do
     (def
       ~(with-meta fname {:doc doc-string :arglists `'(~args)})
       (with-meta (fn ~fname ([~@args]
                              ~@body))
                  ~validator-meta-data))))

(defn matches?
  "Checks a string representation of value against regex and
  returns true if value matches the regex. If value is not a
  match then returns nil."
  [regex value]
  (when (re-seq regex (str value))
    true))

(defvalidator
  "Checks an input value to see if it is a valid email address"
  email?
  {:type :email :default-message "Email address %s is invalid."}
  [value]
  (matches? #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$" value))

(defvalidator
  "Checks an input value to see if it is a valid zip code."
  zip-code?
  {:type :zip-code :default-message "Zip code %s is invalid."}
  [value]
  (matches? #"^[0-9]{5}(-[0-9]{4})?$" value))

(defvalidator
  "Checks an input value to see if it is a valid uk post code."
  post-code?
  {:type :post-code :default-message "Post code %s is invalid."}
  [value]
  (matches? #"(?i)^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$" value))

(defn validation-set
  "Takes a sequence (col) that represents
  keys to validate and the functions used to validate them.

  e.g. [:email email? :post-code post-code?]

  Returns a sequence of functions merged with meta data used by
  the validation."
  [col]
  {:pre [(even? (count col))]}
  (map #(let [func (second %)]
          (with-meta
            func
            (merge (meta func) {:target (first %)}))) (partition 2 col)))

(defn validate
  "Takes a validation set an applies it to m"
  [v-set m]
  (let [valids (map (fn [v]
                      (let [value (get-in m [(:target (meta v))])
                            message (:default-message (meta v))]
                        {:valid? (v value)
                         :message #+clj (format message value)
                                  #+cljs (.replace message "%s" value)})) v-set)]
    {:valid? (reduce #(and (:valid? %1) (:valid? %2)) {:valid? true} valids)
     :results (map :message (filter #(not (:valid? %)) valids))}))
