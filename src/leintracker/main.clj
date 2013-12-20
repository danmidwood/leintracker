(ns leintracker.main
  (:gen-class)
  (:require [ring.adapter.jetty :as ring]
            [leintracker.web.web :as web]
            [taoensso.timbre :as log]))

(defn start [port]
  (log/info "Starting server on port: " port)
  (ring/run-jetty (var web/app)
                  {:port (or port 3000) :join? false}))

(defn -main
  ([] (-main 8080))
  ([port]
     (let [sys-port (System/getenv "PORT")]
       (if (nil? sys-port)
	 (start (cond
                 (string? port) (Integer/parseInt port)
		 :else port))
	 (start (Integer/parseInt sys-port))))))
