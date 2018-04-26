(ns clova.util
  #?@(:clj
       [(:require 
          [clj-time.coerce :as c] 
          [clj-time.format :as f])]
       :cljs 
       [(:require 
          [cljs-time.coerce :as c] 
          [cljs-time.format :as f])]))

(def not-nil? (complement nil?))

(defn missing?
  "If value is equal to :clova.core/key-not-found?
  then returns true oterwise returns false."
  [value]
  (= :clova.core/key-not-found? value))

(def not-missing? (complement missing?))

(defn not-nil-or-missing?
  "If the value is not nil and not equal to :clova.core/key-not-found?
  then returns true, otherwise returns false."
  [value]
  (and (not-missing? value)
       (not-nil? value)))

(defn func-or-default
  "If the result of calling f is not nil
  then returns the result, otherwise returns default."
  [f default]
  (if-let [r (f)]
    r
    default))

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
  (fn? x))

(defn to-clj-date
  "Turns value into a clj-time date."
  ([value]
   (to-clj-date value nil))
  ([value formatter]
   (let [formatter (if (string? formatter)
                     (f/formatter formatter)
                     formatter)
         from-str #(if (not-nil? formatter)
                     (f/parse formatter %)
                     (f/parse %))]
     #?(:clj (if (instance? java.util.Date value)
               (c/from-date value)
               (if (instance? org.joda.time.DateTime value)
                 value
                 (from-str value))))
     #?(:cljs (if (instance? js/Date value)
                (c/from-date value)
                (if (or (instance? goog.date.Date value)
                        (instance? goog.date.DateTime value))
                  value
                  (from-str value)))))))

(defn realise-args
  "Given a sequence of arguments (args) will evaluate any argument
  that is a function by passing in value, or will leave non functional arguments in place."
  [args value]
  (map (fn [arg] 
         (if (function? arg)
           (arg value)
           arg)) args))

(defn validated-map
  "Given a sequence of validation results returns a decorated map with
  validation results transposed to applicable keys or the original map (m)
  if results is empty."
  [m results]
  (if (empty? results)
    m
    (merge 
      {:clova.core/results (map :message results) 
       :clova.core/invalid? (some (partial not) (map :valid? results))}
      (reduce (fn [acc i] (assoc-in acc (key i) (map :message (val i)))) m (group-by :target results)))))

(defn map-some
  "Behaves exactly like map but when `short-circuit?` is true
  uses `some` and wraps the first non nil value in a vector."
  [short-circuit? f col]
  (if short-circuit?
    [(some f col)]
    (map f col)))
