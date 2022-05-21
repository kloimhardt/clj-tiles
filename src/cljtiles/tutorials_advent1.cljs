(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]
            [clojure.string :as str]
            [cljtiles.utils :as utils]
            [cljs.reader :as edn]
            [clojure.walk :as walk]))

;; load exports none, e.g. Cartan is needed, read also crashing #17
;;#+begin_src clojure :noweb yes
;;<<Cartan>>
;;#+end_src

;;#19
;;No method in multimethod 'sicmutils.gener
;;ic/mul' for dispatch value: [:sicmutils.s

;;------------

;; include load file in context menu (with input field and a default box with urls from Sam's fdg)

;; make sure sucmutils env is loaded so that all puzzles run

;; make ->tex-equation work with TeX (tutorial 5)

;;------------

;; move all the defs (chaps etc) into data-store and provide data-store as an atom to components

;; read 1/2 as 1/2 and not 0.5

;; update spec, respectively replace with malli

;; make inline of only one slot possible in UI context menu (maybe upgrade blockly)

(def url
  "https://raw.githubusercontent.com/mentat-collective/fdg-book/main/clojure/org/chapter001.org")

(defn normal-read-string [s]
  (edn/read-string (str "[" s "]")))

(defn extended-read-string [s]
  (let [nl #{:n1956214 :n2176543} ;;some random keywords used as marker
        nls (str " " (apply str (interpose " " nl)) " ")
        a (str/replace s #";.*?\n" "") ;;remove comment lines
        b (str/replace a #"\n" nls)
        c (normal-read-string b)]
    (->> (into [] (remove nl c)) ;;remove last newline
         (walk/postwalk (fn [x]
                          (if (and (coll? x) (some nl x))
                            (list :tiles/vert
                                  (utils/list-into-same-coll x
                                                             (remove nl x)))
                            x))))))

(defn read-tuts [txt]
  (let [src-split (map #(str/split % #"\#\+begin_src clojure")
                       (str/split txt #"\#\+end_src"))
        names (->> src-split
                   (map first)
                   (map #(some-> (second (str/split % #"\#\+name: ")) str/trim)))
        headers-code (->> src-split
                          (map last)
                          (map #(utils/twosplit % "\n"))
                          (butlast))]
    (map (fn [name [header src]]
           {:name name :header header :src src})
         names headers-code)))

(defn tuts-edn [tuts-txt]
  (map #(assoc % :code (extended-read-string (:src %))) tuts-txt))

(defn replace-reference [tuts-edn]
  (let [name-map (into {}
                       (comp (filter :name)
                             (map (juxt :name identity)))
                       tuts-edn)
        middle (fn [s] (subs (subs s 2) 0 (- (count s) 4)))]
    (map #(let [frs (str (first (:code %)))]
            (if (str/starts-with? frs  "<<")
              (assoc % :code (:code (get name-map (middle frs))))
              %))
         tuts-edn)))

(defn smuggle-shadow [tuts-map]
  (butlast
   (reduce (fn [acc {:keys [header src] :as tut}]
             (let [last (peek acc)
                   vcoll (pop acc)]
               (if (str/ends-with? header ":exports none")
                 (conj vcoll (assoc last :shadow (normal-read-string src)))
                 (-> vcoll
                     (conj (merge last tut))
                     (conj {})))))
           [{}] tuts-map)))

(defn explode-all [tuts-mapvec]
  (map #(-> %
            (assoc :solpos-yx [[0 0]])
            (assoc :solution (:code %))
            (dissoc :name)
            (dissoc :header)
            (dissoc :src)
            (merge (explode/explode (:code %))))
       tuts-mapvec))

(comment
  (->> t
       (read-tuts)
       (tuts-edn)
       (replace-reference)
       (smuggle-shadow)
       (explode-all))

  (def uu (replace-reference (tuts-edn (read-tuts t))))
  (smuggle-shadow uu)
  (reduce (fn [acc {:keys [header src] :as tut}]
            (let [last (peek acc)
                  vcoll (pop acc)]
              (if (str/ends-with? header ":exports none")
                (conj vcoll (assoc last :shadow (normal-read-string src)))
                (-> vcoll
                    (conj (merge last tut))
                    (conj {})))))
          [{}] uu)

  (def z (str/split t #"\#\+end_src"))
  (def y (map #(str/split % #"\#\+begin_src clojure") z))

  (def x (map first y))
  (def names (map #(some-> (second (str/split % #"\#\+name: ")) str/trim) x))

  (def a (map last y))
  (def b (butlast (map #(utils/twosplit % "\n") a)))

  (def d (map conj b names))
  (def c (map (fn [[head txt name]]
                [head (extended-read-string txt) name])
              d))

  (reduce (fn [acc [head edn name]]
            (let [last (peek acc)
                  vcoll (pop acc)]
              (if (str/ends-with? head ":exports none")
                (conj vcoll (assoc last :shadow
                                   {:code edn :name name}))
                (-> vcoll
                    (conj (merge (assoc last :code edn)
                                 (when name {:name name})))
                    (conj {})))))
          [{}] c)

  :end)

(defn generate-content-and-call1 [txt init-fn]
  (def t txt)
  (let [tuts
        (->> (str/split txt #"\#\+end_src")
             (map #(last (str/split % #"\#\+begin_src clojure")))
             (map #(utils/twosplit % "\n"))
             (filter (complement #(str/ends-with? (first %) ":exports none")))
             (map second))
        tuts-start 0
        nof-read-tuts 20 ;;(dec (count tuts))
        a (take nof-read-tuts (drop tuts-start tuts))
        ;; b (map #(edn/read-string (str "[" % "]")) a) ;;version without :tiles/vert
        b (map extended-read-string a)
        c (map #(assoc (explode/explode %)
                       :solpos-yx [[0 0]]
                       :solution %) b)
        content {:tutorials c :chapnames ["Advent"] :chaps [(count c)]}]
    (init-fn [content])))

(defn generate-content-and-call [txt init-fn]
  (let [tuts
        (->> txt
             (read-tuts)
             (tuts-edn)
             (replace-reference)
             (smuggle-shadow)
             (explode-all))
        content {:tutorials tuts :chapnames ["Advent"] :chaps [(count tuts)]}]
    (init-fn [content])))

(defn init-advent [init-fn]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(generate-content-and-call % init-fn))))
