(ns user
  (:require
   [clojure.tools.namespace.repl :refer :all]))

(when (System/getProperty "clova.load_nrepl")
  (require 'nrepl))

(when (System/getProperty "clova.codox")
  (require 'codox))
