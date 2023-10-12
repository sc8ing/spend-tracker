(ns spend-tracker.rest
  (:require [compojure.core :as compojure]
            [compojure.route :as route]))

(defn- handle-record-transaction [{:keys [amount category]}]
  (println "recording transaction: " amount category)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "success"})

(compojure/defroutes routes
  (compojure/POST "/transaction" {params :params} (handle-record-transaction params))
  (route/not-found "Not Found"))

