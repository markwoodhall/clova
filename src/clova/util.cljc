(ns clova.util)

(def not-nil? (complement nil?))
(defn not-nil-or-missing?
  [value]
  (and (not= :clova.core/key-not-found? value)
       (not-nil? value)))
