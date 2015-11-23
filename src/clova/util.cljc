(ns clova.util)

(def not-nil? (complement nil?))

(defn not-missing?
  [value]
  (not= :clova.core/key-not-found? value))

(defn not-nil-or-missing?
  [value]
  (and (not-missing? value)
       (not-nil? value)))

(defn as-seq
  "If value is sequential? then just return it. If not
  then wrap it in a vector."
  [value]
  (if (sequential? value)
    value
    [value]))

(defn- get-possibly-unbound-var
  "Like var-get but returns nil if the var is unbound."
  [v]
  (try (var-get v)
       (catch IllegalStateException e
         nil)))

(defn function?
  "Returns true if argument is a function or a symbol that resolves to
  a function (not a macro)."
  [x]
  (if (symbol? x)
    (when-let [v (resolve x)]
      (when-let [value (get-possibly-unbound-var v)]
        (and (fn? value)
             (not (:macro (meta v))))))
    (fn? x)))
