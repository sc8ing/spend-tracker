(defproject spend-tracker "0.1.0-SNAPSHOT"
  :description "Simple spend tracking app"
  :url "https://fatobesegoo.se"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [hiccup "2.0.0-RC2"]
                 [compojure "1.7.0"]
                 [org.clojure/data.json "2.4.0"]]
  :main ^:skip-aot spend-tracker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
