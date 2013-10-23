(ns dependencything.main
  (:use dependencything.web)
  (:require [ring.adapter.jetty :as ring]))


(defn start [port]
  (ring/run-jetty (var app)
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
