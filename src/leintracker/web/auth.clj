(ns leintracker.web.auth
  (:use compojure.core)
  (:require [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [ring.util.response :as resp]
            [ring.util.codec :as codec]
            [environ.core :refer [env]]))

(def ^:private auth-callback-path "/authcb")

(def ^:private client-config
  {:client-id (env :leintracker-gh-client-key)
   :client-secret (env :leintracker-gh-client-secret)
   :callback {:domain (env :leintracker-gh-callback-domain)
              :path auth-callback-path}})

(def ^:private uri-config
  {:authentication-uri {:url "https://github.com/login/oauth/authorize"
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (oauth2/format-config-uri client-config)
                                :scope ""}}
   :access-token-uri {:url "https://github.com/login/oauth/access_token"
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (oauth2/format-config-uri client-config)
                              :code ""}}})

(defn redirect-to-home [req] (resp/redirect (str (:context req) "/")))

(defn redirect-to-user [req] (resp/redirect (str (:context req) "/user")))

(def read-identity friend/identity)

(defroutes github-routes-unsecure
  (GET "/logout" req
       (friend/logout* (redirect-to-home req)))
  (GET "/login" req
       (friend/authenticated
        (redirect-to-home req))))

(defroutes auth-routes
  (friend/authenticate
   github-routes-unsecure
   {:allow-anon? true
    :default-landing-uri "/"
    :login-uri auth-callback-path
    :unauthorized-handler #("You do not have sufficient privileges to access " (:uri %))
    :workflows [(oauth2/workflow
                 {:client-config client-config
                  :uri-config uri-config
                  :config-auth {}
                  :access-token-parsefn #(-> %
                                             :body
                                             codec/form-decode
                                             (get "access_token"))})]}))
