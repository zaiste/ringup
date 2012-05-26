(ns ringup.core
  (:require [ring.adapter.jetty :as ring])
  (:use ring.middleware.params
        ring.middleware.keyword-params
        ring.middleware.resource
        ring.middleware.file-info
        ring.middleware.session
        ring.middleware.session.cookie
        ring.util.response))

(defn handler [request]
  {:status 200 
   :headers {"Content-Type" "text/plain"}
   :body "Hello, Ring!"})

(defn handler2 [request] 
  "Abstracted response creation"
  (-> 
    (response "Bonjour, Ring!")
    (content-type "text/plain")))

(defn handler3 [request]
  "Serving files TODO: Not working"
  (file-response "readme.md", {:root "public"}))

(defn handler4 [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (-> (response (str "You accessed this page " count " times."))
      (assoc :session session))))

(defn handler5 [request]
  "Show IP address"
  {:status 200 
   :headers {"Content-Type" "text/plain"}
   :body (:remote-addr request)})

;; Middleware

(defn wrap-content-type [handler content-type]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Content-Type"] content-type))))

(defn show-params [handler]
  (fn [request]
    (let [response (handler request)]
      (println (str request))
      response))) 

;; Main

(def app 
  (-> handler4
    (show-params)
    (wrap-session {:store (cookie-store) :cookie-attrs {:max-age 15}})
    (wrap-resource "public")
    (wrap-file-info)
    (wrap-content-type "text/plain")
    (wrap-keyword-params)
    (wrap-params)))

(defn start [port]
  (ring/run-jetty #'app {:port 3456 :join? false}))


(defn -main
  "I start servers... usually"
  [& args]
  (start "3456"))
