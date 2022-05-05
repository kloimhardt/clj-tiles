(ns cljtiles.utils
  (:require [clojure.string :as str]))

(defn find-biggest-in-str [sol puzz]
  (some->> (js/LCSubStr sol puzz)
       (re-seq #"(\((.+)\)|\[(.+)\]|\{(.+)\})")
       (map first)
       (group-by count)
       (into (sorted-map))
       last
       val
       first))

(defn condense [erg]
  (reduce (fn [[all-found all-sol] [found sol-vec]]
            (if found
              [(conj all-found found) (concat all-sol sol-vec)]
              [all-found all-sol]))
          [[] []] erg))

(defn find-biggest-in-str-vec [sol-vec puzz]
  (condense
   (map (fn [s]
          (let [found (find-biggest-in-str s puzz)]
            [found (str/split s found)]))
        sol-vec)))

(defn find-biggest-fn [puzz]
  (fn [[found-vec sol-vec]]
    (let [[new-found-vec new-sol-vec]
           (find-biggest-in-str-vec sol-vec puzz)]
      [(concat found-vec new-found-vec) new-sol-vec])))

(defn find-contents [sol puzz]
  (let [fun (find-biggest-fn puzz)]
    (->> [[] [sol]]
         (iterate fun)
         (drop-while #(seq (second %)))
         ffirst)))

(defn get-symbols [s]
  (-> s
      (str/replace #"[\(\)\[\]\{\}]" "")
      (str/split " ")))

(defn remove-first-from-coll [coll el]
  (let [[f s] (split-with (complement #{el}) coll)]
    (concat f (rest s))))

(defn find-lacking [sol puzz]
  (->> (reduce remove-first-from-coll (get-symbols puzz) (get-symbols sol))
       (filter seq)))

(comment

  (def sol "g (a b c d) koi {oi p [ha k u] r i j} uio [h a l l o]")
  (def puzz "(a b c d) kuuu {oi p [ha k u] r i j} koi kaba [h a l l o]")
  (def cont (find-contents sol puzz))
  (def lack (find-lacking sol puzz))

  :end)

(defn newsplit [ss s]
  (let [erg (str/split ss s)]
    (cond
      (nil? ss) [""]
      (nil? s) [ss]
      (not (seq s)) erg
      (str/ends-with? ss s)  (conj erg "")
      :else erg)))

(comment
  (defn uu [a b]
    [(str/split a b) (newsplit a b)])
  (str/split "a" nil)
  (uu "a" nil)
  (uu nil nil)
  (uu nil "a")
  (uu "ab" "b") ;; => [["a"] ["a" ""]] only case it differs, all others same in cljs
  (uu "a" "b")
  (uu "ba" "b")
  (uu "" "a")
  (uu "a" "")
  (uu "" "")
  :end)

(defn stringsearch [ss s]
  (->> (map #(subs ss % (+ % (count s))) (range))
       (take-while seq)
       (take-while #(not= s %))
       count))

(defn twosplit-sfn [ss s searchfun]
  (cond
    (nil? ss) [""]
    (nil? s) [ss]
    (not (seq s)) [ss]
    :else
    (let [posi (searchfun ss s)]
      (if (= posi (count ss))
        [ss]
        [(subs ss 0 posi) (subs ss (+ posi (count s)))]))))

(defn twosplit [ss s] (twosplit-sfn ss s stringsearch))

(defn twosplit-word [ss s]
  (twosplit-sfn ss s
                (fn [ss s]
                  (if (re-find (re-pattern (str "\\b" s "\\b")) ss)
                    (stringsearch ss s)
                    (count ss)))))

(defn nsplit [ss s]
  (let [f s] (twosplit ss s)
       (if (seq s)))
  )

(comment
  (defn uuv [a b]
    [(str/split a b) (twosplit a b)])

  (uuv "a" nil)
  (uuv nil nil)
  (uuv nil "a")
  (uuv "ab" "b") ;; => [["a"] ["a" ""]] only case it differs, all others same in cljs
  (uuv "a" "b")
  (uuv "ba" "b")
  (uuv "" "a")
  (uuv "a" "")
  (uuv "" "") ;; here it differs as well
  (uuv "(a)" "(a)")
  (uuv "a b" "c")
  :end)

(defn text->exps [text [word & words]]
  (let [[fir sec] (twosplit text word)]
    (cond
      (and sec words) (concat (text->exps fir words)
                              (cons word (text->exps sec words)))
      words (text->exps fir words)
      sec (list fir word sec)
      :else (list fir))))

(defn textlist->exps [textlist words]
  (mapcat #(text->exps % words) textlist))

(comment
 (def te (text->exps puzz cont))
 (textlist->exps te lack)

 :end)

(defn s-i-e-recur2 [ml pl color-kw]
  (if (seq ml)
    (cond->> (map (fn [p]
                    (if (= :yellow (:color p))
                      (s-i-e-recur2 (rest ml)
                                    (map (fn [txt]
                                           {:color :yellow :text txt})
                                         (newsplit (:text p)
                                                   (first (rest ml))))
                                    color-kw)
                      p))
                  pl)
      (first ml) (interpose {:color color-kw :text (first ml)}))
    pl))

(defn split-into-expressions2 [puzz exprs color-kw]
  (flatten (s-i-e-recur2 (cons nil exprs) puzz color-kw)))

(defn split-into-expressions2-initial [puzz exprs color-kw]
  (split-into-expressions2 [{:text puzz :color :yellow}]
                           exprs
                           color-kw))

(defn paint [t c]
  {:color c :text t})

(defn text->colexps [text [word & words] color]
  (if (not= :yellow (:color text))
    (list text)
    (let [[fir sec] (map #(paint % :yellow)
                         (if (= color :black)
                           (twosplit-word (:text text) word)
                           (twosplit (:text text) word)))]
      (cond
        (and sec words)
        (concat (text->colexps fir words color)
                (cons (paint word color)
                      (text->colexps sec words color)))
        sec (list fir (paint word color) sec)
        words (text->colexps fir words color)
        :else
        (list fir)))))

(defn textlist->colexps [textlist words color]
  (mapcat #(text->colexps % words color) textlist))

(def cg (text->colexps (paint puzz :yellow) cont :green))
(textlist->colexps cg lack :black)

(defn mark-green-and-black [puzz green-exprs black-exprs]
  (-> (text->colexps (paint puzz :yellow) green-exprs :green)
      (textlist->colexps black-exprs :black)))

(defn split-into-words [expressions]
  (mapcat (fn [exp]
            (map (fn [word] {:color (:color exp) :text word})
                 (newsplit (:text exp) " ")))
          expressions))

(defn remove-empty [words]
  (filter #(seq (:text %)) words))

(defn split-at-rec [[n & ns] coll]
  (let [[f s] (split-at n coll)]
    (if ns
      (cons f (split-at-rec ns s))
      (list f s))))

(comment
  (def sol "g (a b c d) koi {oi p [ha koi u] kaba i j} uio (dist [h a l l o])")
  (def puzz "(a b c d) (a b c d) kuuu dist {oi p [ha koi u] r i j} koi kaba (dist [h a l l o])")
  (def cont (find-contents sol puzz))
  (def lack (find-lacking sol puzz))

  (def exps (mark-green-and-black puzz cont lack))

  (text->colexps (paint "a b c d a b c d" :yellow) ["b" "c" "d" "a"] :black)

  (mark-green-and-black puzz [] lack)
  (mark-green-and-black puzz cont [])
  (mark-green-and-black puzz [] [])
  (def words (remove-empty (split-into-words exps)))
  (def nls [2 3 4])
  (split-at-rec nls words)

  :end)
