(ns spend-tracker.core
  (:require [clojure.pprint :refer [pprint]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [hiccup2.core :as h]
            [compojure.core :as compojure]
            [compojure.route :as route])
  (:gen-class))

(defn in-page [content &{:keys [title] :or {title "Spend Tracker"}}]
  (h/html
    [:head
      [:title "Spend Tracker"]
      [:link {:rel "stylesheet" :href "/css/style.css"}]
      [:script {:src "/js/htmx.min.js"}]]
    [:body
     [:h1 title]
     content]))

(def record-txn-input
  (h/html
   [:div [:form {:method "post"
                 :action "/transaction"
                 :hx-post "/transaction"}
          [:input {:type "number"
                   :name "amount"}]
          [:input {:type "text"
                   :name "category-and-tags"}]
          [:input {:type "submit"
                   :value "Add"}]]]))

(defn root-page [request]
  (println "request" (pprint request))
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (-> record-txn-input
             (in-page {:title "Record Transaction"})
             str)})

(defn record-transaction [{:keys [amount category-and-tags]}]
  (println "recording transaction: " amount category-and-tags)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str record-txn-input)})

(compojure/defroutes app-routes
  (compojure/GET "/" _ root-page)
  (compojure/POST "/transaction" {params :params} (record-transaction params))
  (route/not-found "Not Found"))

(def app (-> #'app-routes
             (wrap-keyword-params)
             (wrap-params)
             (wrap-resource "public")))

(defn -main
  [& _]
  (run-jetty #'app {:port 3000
                    :join? false}))
