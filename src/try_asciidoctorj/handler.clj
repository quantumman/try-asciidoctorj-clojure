(ns try-asciidoctorj.handler
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.anti-forgery :refer :all]
            [org.httpkit.server :refer [run-server]]
            [net.cgrand.enlive-html :as html])
  (:import org.asciidoctor.Asciidoctor$Factory
           org.asciidoctor.Attributes
           org.asciidoctor.Options
           java.util.Collections))

(def ^:private ^:static adoc (Asciidoctor$Factory/create))

(html/deftemplate main-template "templates/application.html"
  []
  [:form]
  (html/prepend (html/html-snippet (anti-forgery-field))))

(defn make-options
  [doctype]
  (if-not (clojure.string/blank? doctype)
    (let [attributes (doto (Attributes.) (.setBackend doctype)),
          options (doto (Options.) (.setAttributes attributes))]
      options)
    (Collections/emptyMap)
    ))
(defn render-page
  [doc doctype]
  (.convert adoc doc (make-options doctype)))

(defn index-page
  []
  (main-template))

(defroutes app-routes
  (GET "/" [] (index-page))
  (POST "/" [source doctype] (render-page source doctype))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main [& [port]]
  (let [port (Integer/parseInt (or port (System/getenv "PORT") "8080"))]
    (run-server app {:port port})))
