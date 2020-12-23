(ns cljtiles.tests
  (:require
   [cljtiles.tutorials-0 :as t-0]
   [cljtiles.tutorials-a :as t-a]
   [cljtiles.tutorials-b :as t-b]
   [cljtiles.tutorials-c :as t-c]
   [cljtiles.tutorials-d :as t-d]
   [cljtiles.tutorials-e :as t-e]
   [cljtiles.tutorials-f :as t-f]
   [tubax.core :as sax]
   [cljtiles.xmlparse :as edn->code]))

(def tutorials (concat t-a/vect
                       t-b/vect
                       t-c/vect
                       t-d/vect
                       t-e/vect
                       t-f/vect))

(defn prs [edn-xml]
  (try {:code (edn->code/parse edn-xml)}
       (catch js/Error e {:error (.-message e)})))

(defn generate-code [t]
  (map #(pr-str (prs (sax/xml->clj %))) t))

(defn dotests []
  (let [t0 (generate-code t-0/vect)
        t1 (generate-code tutorials)
        tests (map #(= %1 %2) t0 t1)]
    (def t0 t0)
    (def t1 t1)
    [(if (seq (filter not tests)) "tests failed" "all ok!") tests]))

(comment

  (dotests)
  (map #(= %1 %2) t0 t1)
  [(nth t0 16) (nth t1 16)]
  (= (nth t0 16) (nth t1 16))

  )
