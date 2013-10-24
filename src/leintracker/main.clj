(ns leintracker.main
  (:require [ring.adapter.jetty :as ring]
            [leintracker.web.web :as web]))

(defn start [port]
  (ring/run-jetty (var web/app)
                  {:port (or port 8080) :join? false}))

(defn -main
  ([] (-main 8080))
  ([port]
     (let [sys-port (System/getenv "PORT")]
       (if (nil? sys-port)
	 (start (cond
                 (string? port) (Integer/parseInt port)
		 :else port))
	 (start (Integer/parseInt sys-port))))))
