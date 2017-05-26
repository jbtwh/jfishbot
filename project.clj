(defproject jfishbot "1.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [net.java.dev.jna/jna "4.4.0"]
                 [net.java.dev.jna/jna-platform "4.4.0"]
                 [clj-return-from "1.0.1"]

                 [local/jl1.0.1 "1.0"]
                 [local/jsminim "1.0"]
                 [local/minim "1.0"]
                 [local/mp3spi1.9.5 "1.0"]
                 [local/tritonus_aos "1.0"]
                 [local/tritonus_share "1.0"]

                 ]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :repositories {"local" ~(str (.toURI (java.io.File. "repo")))}
  :resource-paths ["src/main/resources"]
  :jvm-opts ["-Xmx521m" "-Dfile.encoding=UTF-8"]
  )
