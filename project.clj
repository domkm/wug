(defproject com.domkm/wug "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/DomKM/wug"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.combinatorics "0.0.7"]]
  :jar-exclusions [#"\.cljx"]
  :global-vars {*warn-on-reflection* true}
  :source-paths ["target/generated/src" "src"] ; TODO: remove "src" and change extensions to cljx once LightTable supports cljx
  :test-paths ["target/generated/test"]

  :profiles
  {:dev {:dependencies [[org.clojure/clojurescript "0.0-2120"]
                        [lein-light-nrepl "RELEASE"]]
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                           cljx.repl-middleware/wrap-cljx
                                           lighttable.nrepl.handler/lighttable-ops]}
         :plugins [[com.keminglabs/cljx "0.3.2"]
                   [lein-cljsbuild "1.0.2"]
                   [com.cemerick/clojurescript.test "0.2.2"]]
         :hooks [cljx.hooks #_ leiningen.cljsbuild]
         :cljx {:builds [{:source-paths ["src"]
                          :output-path "target/generated/src"
                          :rules :clj}
                         {:source-paths ["src"]
                          :output-path "target/generated/src"
                          :rules :cljs}
                         {:source-paths ["test"]
                          :output-path "target/generated/test"
                          :rules :clj}
                         {:source-paths ["test"]
                          :output-path "target/generated/test"
                          :rules :cljs}]}
         :cljsbuild {:builds [{:source-paths ["target/generated/src" "target/generated/test"]
                               :compiler {:output-to "target/generated/test.js"
                                          :optimizations :advanced
                                          :libs [""]}}]
                     :test-commands {"phantom" ["phantomjs" :runner "target/generated/test.js"]
                                     "node" ["node" :node-runner "target/generated/test.js"]}}
         :aliases {"cleantest" ["do" "clean," "cljx" "once," "test," "cljsbuild" "test"]}}})
