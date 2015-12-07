(defproject clova "0.19.0"
  :description "A simple validation library for Clojure and ClojureScript."
  :url "http://github.com/markwoodhall/clova"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :codox {:metadata {:doc/format :markdown}
          :namespaces [clova.core]
          :source-uri "https://github.com/markwoodhall/clova/blob/master/src/{classpath}#L{line}"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-time "0.11.0"]
                 [com.andrewmcveigh/cljs-time "0.3.14"]
                 [org.clojure/clojurescript "1.7.170"]]
  :jar-exclusions [#"\.swp|\.swo|user.clj"]
  :source-paths ["src"])
