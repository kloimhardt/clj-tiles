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
  (let [src-split-1 (str/split (str txt "\n") #"\#\+end_src")
        re-merge (fn [txtvec inter]
                   (map #(apply str %)
                        (partition-all 2 (interpose inter txtvec))))
        descriptions (re-merge src-split-1 "#+end_src")
        src-split (map #(str/split % #"\#\+begin_src clojure")
                       src-split-1)
        names (->> src-split
                   (map first)
                   (map #(some-> (second (str/split % #"\#\+name: ")) str/trim)))
        headers-code (->> src-split
                          (map last)
                          (map #(utils/twosplit % "\n"))
                          (butlast))]
    (map (fn [name [header src] desc]
           {:name name :header header :src src :description desc})
         names headers-code descriptions)))

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
   (reduce (fn [acc {:keys [header src description] :as tut}]
             (let [last (peek acc)
                   vcoll (pop acc)]
               (if (str/ends-with? header ":exports none")
                 (conj vcoll (-> last
                                 (update :shadow #((fnil conj []) % src))
                                 (update :shadow-description #((fnil conj []) % description))))
                 (-> vcoll
                     (conj (merge last tut))
                     (conj {})))))
           [{}] tuts-map)))

(defn replace-inline-tex [s]
  (let [new-str-fn #(str (first %) "\\(" (subs (subs % 2) 0 (- (count %) 4)) "\\)" (last %))]
    (-> s
        (str/replace #"[\. ,\n]\$\S+?\$[\. ,\n:]" new-str-fn)
        (str/replace #"[\. ,\n]\$\S+ = \S+?\$[\. ,\n:]" new-str-fn)
        (str/replace #"[\. ,\n]\$\S+ \S+?\$[\. ,\n:]" new-str-fn))))

(defn format-description [tuts-mapvec]
  (let [descvec (->> tuts-mapvec
                     (map (fn [d]
                            (str (apply str (:shadow-description d))
                                 (:description d)))))
        pre-and-p (->> descvec
                       (map #(str/split % #"\n\n"))
                       (map #(into [:div]
                                   (map (fn [[previ prg]]
                                          (cond
                                            (str/starts-with? prg "#+RESULTS") nil
                                            (str/starts-with? prg "#+STARTUP") nil
                                            (str/starts-with? prg "#+PROPERTY") nil
                                            (str/starts-with? prg "#+begin_src clojure :exports none") nil

                                            (and (str/starts-with? previ "#+begin_src clojure :exports none")
                                                 (str/starts-with? (last (str/split prg #"\n")) "#+end_src"))
                                            nil

                                            (str/starts-with? prg "#+") [:pre prg]
                                            (str/starts-with? (last (str/split prg #"\n")) "#+") [:pre prg]

                                            (str/starts-with? prg "* ")
                                            [:h1 (replace-inline-tex (subs (first (str/split prg #"\n")) 2))]

                                            (str/starts-with? prg "** ")
                                            [:h2 (replace-inline-tex (subs (first (str/split prg #"\n")) 3))]

                                            :else
                                            [:p (replace-inline-tex prg)]))
                                        (partition 2 1 (cons "" %))))))]
    (map #(assoc %1 :description %2) tuts-mapvec pre-and-p)))

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
                  (format-description)
                  (explode-all))
        content {:tutorials tuts :chapnames [(re-find #"[ \w-]+?(?=\.)" (guri/getPath url))] :chaps [(count tuts)]}]
    (init-fn [content])))

(defn init [url init-fn]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(generate-content-and-call % init-fn url))))
