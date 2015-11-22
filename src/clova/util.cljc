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
