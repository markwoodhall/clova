(defproject clova "0.37.0"
  :description "A simple validation library for Clojure and ClojureScript."
  :url "http://github.com/markwoodhall/clova"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :codox {:metadata {:doc/format :markdown}
          :namespaces [clova.core]
          :source-uri "https://github.com/markwoodhall/clova/blob/master/src/{classpath}#L{line}"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-time "0.14.2"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [org.clojure/clojurescript "1.9.946"]]
  :jar-exclusions [#"\.swp|\.swo|user.clj"]
  :source-paths ["src"])
