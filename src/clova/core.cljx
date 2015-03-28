(ns clova.core)

(defn valid-email?
  "Checks an input string to see if it is a
  valid email address."
  [s]
  (not-empty (re-seq #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$" s)))
