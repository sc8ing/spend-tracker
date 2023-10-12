(ns spend-tracker.core
  (:require
   [compojure.core :as compojure]
   [compojure.route :as route]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.resource :refer [wrap-resource]]
   [spend-tracker.htmx :as htmx]
   [spend-tracker.rest :as rest])
  (:gen-class))

(compojure/defroutes app-routes
  (compojure/GET "/" _ htmx/root-page)
  (compojure/context "/rest" [] rest/routes)
  (compojure/context "/htmx" [] htmx/routes)
  (route/not-found "Not Found"))

(def app (-> #'app-routes
             (wrap-keyword-params)
             (wrap-params)
             (wrap-resource "public")))

(defn -main [& _]
  (run-jetty app {:port 3000}))

(comment
  (def server (run-jetty #'app {:port 3000 :join? false})))
