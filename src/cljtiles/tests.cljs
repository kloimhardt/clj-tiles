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
   [cljtiles.xmlparse-2 :as edn->code-2]
   [clojure.string :as cs]
   [clojure.data :as cd]
   ["blockly" :as blockly]))

(def tutorials (concat t-a/vect
                       t-b/vect
                       t-c/vect
                       t-d/vect
                       t-e/vect
                       t-f/vect))

(defn pipethrough [xml-str]
  (.. blockly/Xml
      (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-str))
                                    (.getMainWorkspace blockly)))
  (->> (.-mainWorkspace blockly)
       (.workspaceToDom blockly/Xml)
       (.domToPrettyText blockly/Xml)))

(defn gen-hiccup [xml-str pipeit!?]
  (sax/xml->clj (if pipeit!? (pipethrough xml-str) xml-str)))

(defn edn->code-parse [hccp strict? orig?]
  (let [parse-function (if orig? edn->code-orig/parse edn->code/parse)
        clj-code (if strict?
                  (parse-function hccp)
                  (try (parse-function hccp)
                       (catch js/Error e {:error (.-message e)})))]
    (or (:dat clj-code) [clj-code])))

(defn analyze [tests failed ok]
  [(count tests) (if (seq (filter not tests)) failed ok) tests])

(defn test-original [pipeit!?]
  (let [t0 (mapv #(edn->code-parse (gen-hiccup % pipeit!?) false false)
                 t-0/vect)
        temp1 (mapv #(edn->code-parse (gen-hiccup % false) false true)
                    tutorials)
        t1 (update-in temp1 [(dec (count temp1)) 3 2 1] assoc (symbol ":id")
                      "rocket")
        tests (map #(= %1 %2) t0 t1)]
    (def t1 t1)
    (def t0 t0)
    (analyze tests "orig tests failed" "test orig all ok!")))

(defn repls [s]
  (-> (str s)
      (cs/replace #"\(clojure.core/deref app-state\)" "@app-state")
      (cs/replace #"\[\]" "[ ]")))

(defn test-consistency [pipeit!?]
  (let [orig t-0s/vect-code
        proc (mapv #(edn->code-parse (gen-hiccup % pipeit!?) true false)
                   t-0s/vect)
        tests (map #(= (repls %1) (str %2)) orig proc)]
    (def orig orig)
    (def proc proc)
    (analyze tests "con tests failed" "test con all ok!")))

(defn test-pipe! []
  (-> (map #(= (assoc (gen-hiccup % true) :attributes {})
               (gen-hiccup % false)) t-0s/vect)
      (analyze "pipe tests failed"
               "pipe all ok!")))

(defn edn->code-parse-2 [hccp strict? orig?]
  (let [parse-function (if orig? edn->code-orig/parse edn->code-2/parse)
        clj-code (if strict?
                   (parse-function hccp)
                   (try (parse-function hccp)
                        (catch js/Error e {:error (.-message e)})))]
     clj-code))

(defn test-consistency-2 [pipeit!?]
  (let [orig t-0s/vect-code
        proc (mapv #(edn->code-parse-2 (gen-hiccup % pipeit!?) true false)
                   t-0s/vect)
        tests (map #(= (repls %1) (str %2)) orig proc)]
    (def orig2 orig)
    (def proc2 proc)
    (analyze tests "con-2 tests failed" "test con-2 all ok!")))

(defn test-break?-2 [pipeit!?]
  (let [proc (mapv #(edn->code-parse-2 (gen-hiccup % pipeit!?) true false)
                   t-0/vect)]
    (def proc3 proc)))

(defn test-pure []
  (map second [(test-consistency false)
               (test-original false)
               (test-consistency-2 false)]))

(defn test-all-pipe! []
  (map second [(test-pipe!)
               (test-consistency true)
               (test-original true)
               (test-consistency-2 true)]))


(comment
  (test-consistency-2 true)
  (test-break?-2 false)

  (test-all-pipe!)
  (test-pure)

  (test-original false)

  [(str (nth t0 40)) (str (nth t1 40))]
  (= (nth t0 49) (nth t1 49))

  (test-consistency false)

  [(repls (last orig))
   (str (last proc))]

  (test-pipe!)

  (cd/diff (assoc (sax/xml->clj (pipethrough (t-0s/vect 24))) :attributes {})
       (sax/xml->clj (t-0s/vect 24)))

  (test-consistency true)
  (test-original true)

  )
