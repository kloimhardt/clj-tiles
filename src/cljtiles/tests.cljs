(ns cljtiles.tests
  (:require
   [cljtiles.genblocks :as gb]
   [cljtiles.tutorials-0 :as t-0]
   [cljtiles.tutorials-a :as t-a]
   [cljtiles.tutorials-b :as t-b]
   [cljtiles.tutorials-c :as t-c]
   [cljtiles.tutorials-d :as t-d]
   [cljtiles.tutorials-e :as t-e]
   [cljtiles.tutorials-f :as t-f]
   [tubax.core :as sax]
   [cljtiles.view :as view]
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
  (map #(prs (sax/xml->clj %)) t))

(defn dotests []
  (let [t0 (generate-code t-0/vect)
        t1 (generate-code tutorials)
        tests (map #(= %1 %2) t0 t1)]
    (def t0 t0)
    (def t1 t1)
    [(count tests) (if (seq (filter not tests)) "tests failed" "all ok!") tests]))

(comment

  (dotests)
  (map #(= %1 %2) t0 t1)
  [(str (nth t0 47)) (str (nth t1 47))]
  (= (nth t0 40) (nth t1 40))

  (edn->code/parse
    (sax/xml->clj (gb/rpg [[0 0] [0 100] [0 170] [100 170] [0 220]]
                          {:title "Getting Clojure"
                           :author "Russ Olson"
                           :published 2018}
                          )))

  (edn->code/parse (sax/xml->clj (gb/rpg [[0 0] [0 100] [0 170] [100 170] [0 220]]
                             {:title "Getting Clojure"
                              :published 2018}
                             )))
  (edn->code/parse
    (sax/xml->clj
      (gb/page [[]]
               (gb/t-map [:title (gb/text "Getting Clojure")]
                         [:published (gb/num 2018)]))))

  (edn->code/level4a
    (edn->code/level3a
      (edn->code/level2a
        (edn->code/level1b
          (sax/xml->clj
            (gb/page [[]]
                     (gb/t-map [:a (gb/text "Getting Clojure")]
                               [:p (gb/text "2018")])))))))
  (edn->code/level4a
    (edn->code/level3a
      (edn->code/level2a
        (edn->code/level1b
          view/hxml))))
  )
