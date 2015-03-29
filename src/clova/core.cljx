(ns clova.core)

(defn email?
  "Checks an input i to see if it is a
  valid email address."
  [i]
  (re-seq #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$" (str i)))

(defn zip-code?
  "Checks an input i to see if it is a
  valid zip code."
  [i]
  (re-seq #"^[0-9]{5}(-[0-9]{4})?$" (str i)))

(defn post-code?
  "Checks an input i to see if it is a
  valid uk post code."
  [i]
  (re-seq #"(?i)^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$" (str i)))
