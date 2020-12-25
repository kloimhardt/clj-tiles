(ns cljtiles.tests
  (:require
   [cljtiles.tutorials-0 :as t-0]
   [cljtiles.tutorials-0s :as t-0s]
   [cljtiles.tutorials-a :as t-a]
   [cljtiles.tutorials-b :as t-b]
   [cljtiles.tutorials-c :as t-c]
   [cljtiles.tutorials-d :as t-d]
   [cljtiles.tutorials-e :as t-e]
   [cljtiles.tutorials-f :as t-f]
   [tubax.core :as sax]
   [cljtiles.xmlparse :as edn->code]
   [cljtiles.xmlparse-orig :as edn->code-orig]
   [clojure.string :as cs]
   ["blockly" :as blockly]))

(def tutorials (concat t-a/vect
                       t-b/vect
                       t-c/vect
                       t-d/vect
                       t-e/vect
                       t-f/vect))

(defn prs [edn-xml]
  (try {:code (edn->code/parse edn-xml)}
       (catch js/Error e {:error (.-message e)})))

(defn prs-orig [edn-xml]
  (try {:code (edn->code-orig/parse edn-xml)}
       (catch js/Error e {:error (.-message e)})))

(defn generate-code [t]
  (map #(prs (sax/xml->clj %)) t))

(defn generate-code-orig [t]
  (map #(prs-orig (sax/xml->clj %)) t))

(defn dotests []
  (let [t0 (generate-code t-0/vect)
        temp1 (vec (generate-code-orig tutorials))
        t1 (update-in temp1 [(dec (count temp1)) :code :dat 3 2 1] assoc (symbol ":id") "rocket")
        tests (map #(= %1 %2) t0 t1)]
    (def temp1 temp1)
    (def t0 t0)
    (def t1 t1)
    [(count tests) (if (seq (filter not tests)) "tests failed" "all ok!") tests]))

(defn get-code [v]
  (or (-> v :code :dat) [(-> v :code)]))

(defn repls [s]
  (-> (str s)
      (cs/replace #"\(clojure.core/deref app-state\)" "@app-state")
      (cs/replace #"\[\]" "[ ]")))

(defn pipethrough [xml-text]
  (.. blockly/Xml
      (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-text))
                                    (.getMainWorkspace blockly)))
  (->> (.-mainWorkspace blockly)
       (.workspaceToDom blockly/Xml)
       (.domToPrettyText blockly/Xml))
  )

(defn test-consistency []
  (let [orig t-0s/vect-code
        proc (mapv #(get-code (prs (sax/xml->clj %)))  t-0s/vect)
        tests (map #(= (repls %1) (str %2)) orig proc)]
    (def orig orig)
    (def proc proc)
    [(count tests) (if (seq (filter not tests)) "con tests failed" "con all ok!") tests]
    )
  )

(comment
  (test-consistency)

  [(repls (last orig))
   (str (last proc))
   ]

  (dotests)

  [(str (nth t0 40)) (str (nth t1 40))]
  (= (nth t0 49) (nth t1 49))

  )
