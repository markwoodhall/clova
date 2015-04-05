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
  {:type :email :default-message "%s is an invalid value for %s."}
  [value]
  (matches? #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$" value))

(defvalidator
  "Checks an input value to see if it is a valid zip code."
  zip-code?
  {:type :zip-code :default-message "%s is an invalid value for %s."}
  [value]
  (matches? #"^[0-9]{5}(-[0-9]{4})?$" value))

(defvalidator
  "Checks an input value to see if it is a valid uk post code."
  post-code?
  {:type :post-code :default-message "%s is an invalid value for %s."}
  [value]
  (matches? #"(?i)^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$" value))

(defvalidator
  "Checks an input value to see if it is a valid url."
  url?
  {:type :url :default-message "%s is an invalid value for %s."}
  [value]
  (matches? #"^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]" value))

(defvalidator
  "Checks an input value to see if it is between lower and upper."
  between?
  {:type :between :default-message "%s is an invalid value for %s, it must be between %s and %s"}
  [value lower upper]
  (and (>= value lower)
       (<= value upper)))

(defn validation-set
  "Takes a sequence (col) that represents
  keys to validate and the functions used to validate them.

  e.g. [:email email? :post-code post-code?]

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
  "Takes a validation set an applies it to m"
  [v-set m]
  (let [valids (map (fn [v]
                      (let [v-name (:target (meta v))
                            value (get-in m [v-name])
                            args (:args (meta v))
                            message (:default-message (meta v))]
                        {:valid? (apply v value args)
                         :message #+clj (apply format message value (name v-name) args)
                         #+cljs (.replace message "%s" value)})) v-set)]
    {:valid? (reduce #(and (:valid? %1) (:valid? %2)) {:valid? true} valids)
     :results (map :message (filter #(not (:valid? %)) valids))}))
