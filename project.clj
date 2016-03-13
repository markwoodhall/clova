(defproject clova "0.24.0"
  :description "A simple validation library for Clojure and ClojureScript."
  :url "http://github.com/markwoodhall/clova"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :codox {:metadata {:doc/format :markdown}
          :namespaces [clova.core]
          :source-uri "https://github.com/markwoodhall/clova/blob/master/src/{classpath}#L{line}"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.11.0"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [org.clojure/clojurescript "1.7.228"]]
  :jar-exclusions [#"\.swp|\.swo|user.clj"]
  :source-paths ["src"])
