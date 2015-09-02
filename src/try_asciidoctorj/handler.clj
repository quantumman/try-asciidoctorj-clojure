(ns try-asciidoctorj.handler
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [org.httpkit.server :refer [run-server]]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate main-template "templates/application.html"
  []
  )


(defn index-page
  []
  (main-template))

(defroutes app-routes
  (GET "/" [] (index-page))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main [& [port]]
  (let [port (Integer/parseInt (or port (System/getenv "PORT") "8080"))]
    (run-server app {:port port})))
