.PHONY: test test-clj test-cljs

test: test-clj test-cljs

test-clj:
	clojure -A:test

test-cljs:
	clojure -A:test-cljs

deploy: test
	# clj is generated a bad pom for some reason, need to generate manually and tweak for now
	# clj -Spom
	mvn deploy
