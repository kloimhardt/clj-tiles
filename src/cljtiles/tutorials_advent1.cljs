(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]
            [clojure.string :as str]
            [cljtiles.utils :as utils]
            [cljs.reader :as edn]
            [clojure.walk :as walk]
            [goog.uri.utils :as guri]))

;;------------

;;klm TODO

;; parse the :as e alias (str "(require '[sicmutils.env :as e]) #_(" (subs t 4)) out of org-file

;; move all the defs (chaps etc) into data-store and provide data-store as an atom to components

;; read 1/2 as 1/2 and not 0.5

;; update spec, respectively replace with malli

;; make inline of only one slot possible in UI context menu (maybe upgrade blockly)

;;only close the modal when new content is visible in workspace (there is a dely when loading the url)

;;use promises properly and not a callback in function "init" below

;;------------

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
              (let [ref (get name-map (middle frs))]
                (-> %
                    (assoc :code (:code ref))
                    (assoc :src (:src ref))))
              %))
         tuts-edn)))

(defn smuggle-shadow [tuts-map]
  (butlast
   (reduce (fn [acc {:keys [header src] :as tut}]
             (let [last (peek acc)
                   vcoll (pop acc)]
               (if (str/ends-with? header ":exports none")
                 (conj vcoll (update last :shadow (fnil conj []) src))
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
            (merge (explode/explode (:code %) nil)))
       tuts-mapvec))

(defn generate-content-and-call [txt init-fn url]
  (let [tuts (->> txt
                  (read-tuts)
                  (tuts-edn)
                  (replace-reference)
                  (smuggle-shadow)
                  (explode-all))
        content {:tutorials tuts :chapnames [(re-find #"[ \w-]+?(?=\.)" (guri/getPath url))] :chaps [(count tuts)]}]
    (init-fn [content])))

(defn init [url init-fn]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(generate-content-and-call % init-fn url))))

(comment
  (type ((fnil conj []) nil 3))
  :end)
