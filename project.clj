(defproject a.new.thing "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [leiningen-core "2.0.0"]
                 [midje/midje "1.4.0" :scope "test"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.4"]]}}
  :main a.new.thing.core)
