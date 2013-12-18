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
                 [friend-oauth2 "0.1.1"]
                 [environ "0.4.0"]
                 [tentacles "0.2.5"]
                 [cornet "0.1.0"]
                 [com.taoensso/timbre "2.6.3"]
                 [korma "0.3.0-RC6"]
                 [postgresql/postgresql "9.1-901.jdbc4"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.4"]]
                   :dependencies [[ring-server "0.3.0"]]
                   :source-paths ["src-dev"]}
             :production {:env {:production true}}}
  :hooks [environ.leiningen.hooks]
  :plugins [[lein-ring "0.8.3"]
            [environ/environ.lein "0.2.1"]
            [s3-wagon-private "1.1.2"]]
  :deploy-repositories [["snapshots" {:url "s3p://jvm-repository/snapshots/"
                                      :creds :gpg}
                         "releases" {:url "s3p://jvm-repositoy/releases/"
                                     :creds :gpg}]]
  :ring {:handler leintracker.web.web/app}
  :main leintracker.main
  :aot [leintracker.main]
  :global-vars {*warn-on-reflection* true}
  :min-lein-version "2.0.0")
