(defproject a.new.thing "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [leiningen-core "2.2.0"]
                 [midje/midje "1.6-alpha2" :scope "test"]
                 [org.clojure/clojure "1.5.1"]
                 [enlive "1.1.1"]
                 [ring "1.2.0"]
                 [net.cgrand/moustache "1.2.0-alpha2"]
                 [compojure "1.1.5"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [cheshire "5.2.0"]
                 [clj-http "0.7.5"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.4"]]}}
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler dependencything.web/app}
  :main dependencything.main)
