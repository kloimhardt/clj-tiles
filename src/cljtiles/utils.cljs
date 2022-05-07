(ns cljtiles.utils
  (:require [clojure.string :as str]
            [clojure.walk :as w]))

(defn find-biggest-in-str [sol puzz]
  ;; sucht grössten gemeinsamen String via LCSubStr
  (some->> (js/LCSubStr sol puzz)
       (re-seq #"(\((.+)\)|\[(.+)\]|\{(.+)\})")
       (map first)
       (group-by count)
       (into (sorted-map))
       last
       val
       first))

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

(defn fullsplit [ss s]
  (let [[eins zwo] (twosplit ss s)]
    (if zwo (cons eins (fullsplit zwo s)) (list eins))))

(defn twosplit-word [ss s]
  ;; splittet nur wenn der String s als Wort gefunden wird
  (twosplit-sfn ss s
                (fn [ss s]
                  (if (re-find (re-pattern (str "\\b" s "\\b")) ss)
                    (stringsearch ss s)
                    (count ss)))))

(comment
  (defn uuv [a b]
    [(str/split a b) (twosplit a b)])

  (uuv "a" nil)
  (uuv nil nil)
  (uuv nil "a")
  (uuv "ab" "b") ;; => [["a"] ["a" ""]] case it differs
  (uuv "w- " " ")
  (uuv "a" "b")
  (uuv "ba" "b")
  (uuv "" "a")
  (uuv "a" "")
  (uuv "" "") ;; here it differs as well
  (uuv "(a)" "(a)")
  :end)

;; need to be all the same atringlength
(def white "w-")
(def green "g-")
(def black "b-")
(def yellow "y-")
(assert (= (count green) (count white) (count black) (count yellow)))

(defn convert-to-color [puzzd-list sold]
  (let [solds (tree-seq coll? seq sold)]
    (map (fn [puzzd]
           (w/prewalk (fn [x]
                        (let [s (pr-str x)
                              found? (some #{x} solds)]
                          (if (coll? x)
                            (if found?
                              (str green s)
                              x)
                            (if found?
                              (str yellow s)
                              (str black s)))))
                      puzzd))
         puzzd-list)))

(defn element-convert-parens [elem]
  (let [open-close-char (fn  [coll]
                          (cond
                            (map? coll) ["{" "}"]
                            (list? coll) ["(" ")"]
                            (vector? coll) ["[" "]"]
                            (set? coll) ["#{" "}"]))
        [open close] (open-close-char elem)]
    (concat [(str white open)]
            (interpose (str white " ") (seq elem))
            [(str white close)])))

(defn convert-parens-to-strings [edn-list]
  (->> edn-list
       (map (fn [edn]
                  (w/prewalk (fn [x]
                               (if (coll? x)
                                 (element-convert-parens x) x))
                             edn)))
       (interpose (str white " "))
       flatten))

(defn expand-greens [strings]
  (map (fn [s] (if (str/starts-with? s green)
                 (->> (str/split (subs s (count green)) #" ")
                      (map #(str green %))
                      (interpose (str green " ")))
                 s))
       strings))

(defn generate-hiccup [strings]
  (letfn [(fgen [s bgc c] [:span {:style {:background-color bgc
                                          :color c}}
                           s])
          (fgreen [s] (fgen s "green" "white"))
          (fwhite [s] (fgen s "white" "black"))
          (fyellow [s] (fgen s "LightYellow" "black"))
          (fblack [s] (fgen s "black" "white"))
          (col-dispatch [s] ((get {green  fgreen
                                   white  fwhite
                                   yellow  fyellow
                                   black  fblack}
                                  (subs s 0 (count green)))
                             (subs s (count green))))]
    (map col-dispatch strings)))

(defn render-colored [code puzz sol]
  ;;puzz is always a vector of code: => ["Hello, World!"]
  (println "in rendercol")
  (def puzz puzz)
  (def sol sol)
  (def code code)
  (->> (convert-to-color puzz sol)
       (convert-parens-to-strings)
       (expand-greens)
       (flatten)
       (generate-hiccup)
       (into [:p])))


(def sold '(g (a b c d) koi {p [ha koi u] kaba i j lua}
              uio (dist [h a l l o])))
(def puzzd '((a b c d) (a b c d) "hi" :lo kuuu dist
             {oi p [ha koi u] r i j}
             koi kaba (dist [h a l l o])))

(defn gneit []
  (render-colored nil puzzd sold))

(def bb
  (->> (convert-to-color puzz sol)
       (convert-parens-to-strings)
       (expand-greens)
       (flatten)
       ))
(identity bb)

(identity code)

(def cc (partition 3 1 bb))

(defn huxi [cc code]
  (println "start")
  (let [firststr (subs (nth (first cc) 0) (count green))
        secondstr (subs (nth (first cc) 2) (count green))
        firstpos (+ (stringsearch code firststr) (count firststr))
        secondpos (+ (stringsearch (subs code firstpos) secondstr) firstpos)
        end (+ secondpos (count secondstr))
        blanks (subs code firstpos secondpos)]
    ;;(println "end " firstpos firststr "||" secondpos secondstr "&&" (pr-str blanks))
    (println code)
    (println "dbg" firststr (pr-str blanks) secondstr)
    (if (seq (rest cc))
        (if (= (nth (first cc) 1) (str white " "))
          (cons blanks (huxi (rest cc) (subs code end)))
          (huxi (rest cc) (subs code firstpos)))
        (if (= (nth (first cc) 1) (str white " "))
          (list blanks)
          (list)))))

(def dd (partition 2 1 (remove #(= % (str white " ")) bb)))

(defn huxi2 [[tuple & r] code]
  (let [[firststr secondstr] (map #(subs % (count green)) tuple)
        firstpos (+ (stringsearch code firststr) (count firststr))
        secondpos (+ (stringsearch (subs code firstpos) secondstr) firstpos)
        blanks (subs code firstpos secondpos)]
    (if (seq r)
      (cons blanks (huxi2 r (subs code secondpos)))
      (list blanks))))

(comment
  (println "----------------")
  (println code)
  (println (pr-str cc))
  (huxi cc code)
  (huxi2 dd code)
(get code 31)
  :end)
(def blanks-puzz (map #(dec (count (fullsplit % " "))) bb))

(def aa (map #(str/split % #" ") (str/split-lines code)))
(def breaks-given (map #(dec (count %)) aa))

(defn sumitup [xs]
  (reduce (fn [acc n] (conj acc (+ n (peek acc)))) [] xs))


(map sumitup [blanks-puzz breaks-given])
(def coll-splits
  (let [sip (sumitup blanks-puzz)]
                   (map (fn [nofblanks] (count (take-while #(<= % nofblanks) sip))) (sumitup breaks-given))))

(defn diffit [xs]
  (reduce (fn [acc n] (conj acc (- n (peek acc)))) [] xs))

(def cs (diffit coll-splits))

(defn split-them [xs posis]
  (let [[fi se] (split-at (first posis) xs)]
    (if (seq (rest posis))
      (cons fi (split-them se (rest posis)))
      (list fi se) ;;se should always be the empty list if exerything gos right
      )))

(defn do-the-split [puzz-strings code]
  (let [blanks-puzz (map #(dec (count (fullsplit % " "))) bb)
        aa (map #(str/split % #" ") (str/split-lines code))
        breaks-given (map #(dec (count %)) aa)
        coll-splits
        (let [sip (sumitup blanks-puzz)]
          (map (fn [nofblanks] (count (take-while #(<= % nofblanks) sip))) (sumitup breaks-given)))
        cs (diffit coll-splits)
        _ :end]
    (split-them puzz-strings cs)))

(do-the-split bb cs)
