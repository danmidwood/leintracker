(defproject leintracker "0.1.0-SNAPSHOT"
  :description "Leiningen Dependency Tracker Web App"
  :url "http://leintracker.com/"
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
                 [clj-http "0.7.5"]
                 [com.cemerick/friend "0.1.5"]
                 [friend-oauth2 "0.0.4"]
                 [environ "0.4.0"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.4"]]}
             :production {:env {:production true}}}
  :hooks [environ.leiningen.hooks]
  :plugins [[lein-ring "0.8.3"]
            [environ/environ.lein "0.2.1"]]
  :ring {:handler leintracker.web/app}
  :main leintracker.main
  :min-lein-version "2.0.0")
