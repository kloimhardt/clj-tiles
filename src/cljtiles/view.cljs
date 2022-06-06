(ns cljtiles.view
  (:require
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [clojure.string :as string]
   [goog.dom.forms :as gforms]
   [goog.uri.utils :as guri]
   [sci.core :as sci]
   [sicmutils.env.sci :as es]
   ["blockly" :as blockly]
   [cljtiles.xmlparse-2 :as edn->code]
   [cljtiles.genblocks :as gb]
   [cljtiles.tutorials-achangin :as t-ac]
   [cljtiles.tutorials-clj :as t-0]
   [cljtiles.tutorials-katas :as t-k]
   [cljtiles.tutorials-sicm2 :as t-s2]
   [cljtiles.tutorials-lagr :as t-l]
   [cljtiles.tutorials-sicm :as t-s]
   [cljtiles.tutorials-sicm3 :as t-s3]
   [cljtiles.tutorials-appen :as t-ax]
   [cljtiles.tutorials-advent1 :as t-adv1]
   [cljtiles.tutorials-fizzbuzz :as t-fizz]
   [cljs.reader :as edn]
   [clojure.walk :as w]
   [tubax.core :as sax]
   [reagent.core :as rc]
   [reagent.dom :as rd]
   [cljtiles.sicm :as sicm]
   [cljtiles.blockly :as workspace!]
   [cljtiles.code-analysis :as ca]
   [cljtiles.components :as cmpnts]
   [cljtiles.codeexplode :as explode]
   [cljtiles.utils :as utils]
   [cljtiles.tests :as tst]
   ;;[flow-storm.api :as fsa]
   ;;[sc.api]
   ;;[cljtiles.sc]
   ))

(def dev false) ;;!! also disable spec!!

(defn rep-amp [xml-text]
  (string/replace xml-text "&" "&amp;"))

(defn generate-xml [_tutorial-no page {:keys [shuffle?]}] ;;tutorial-no is to debug explode
  (if (and (:xml-code page) (not (and shuffle? (:code page))))
    page
    (letfn [(switch-yx [yx-list]
              (when yx-list
                (map (fn [[y x]] [x y]) yx-list)))]
      (let [page (cond
                   ;;this branch is only to debug explode
                   (and false dev #_(< 84 _tutorial-no 120) (:code page))
                   (do
                     ;;(explode/store-dbg-info page)
                     ;;(merge page (explode/explode (:code page)))
                     page
                     #_:end)

                   shuffle?
                   (merge page (explode/explode (:solution page) true))

                   :else
                   page)]
        (-> page
            (assoc :xml-code (rep-amp (apply gb/rpg (or (switch-yx (:blockpos-yx page))
                                                        (:blockpos page))
                                             (:code page))))
            (update :solpos-yx (fnil identity (or (:blockpos-yx page)
                                                  (switch-yx (:blockpos page)))))
            (as-> $ (assoc $ :xml-solution (when (:solution page)
                                             (rep-amp (apply gb/rpg (switch-yx (:solpos-yx $))
                                                             (:solution page)))))))))))

(defn make-content [tuts]
  (let [f (fn [ks v]
            (reduce #(assoc %1 %2 (mapcat %2 v)) {} ks))]
    (when dev
      (run!
       #(let [count-tut (count (:tutorials %))
              sum-chaps (reduce + (:chaps %))]
          (println "tutorial check " (= count-tut sum-chaps) " " count-tut " " sum-chaps))
       tuts))
    (-> (f [:tutorials :chapnames :chaps]
           tuts)
        (update :tutorials #(map-indexed (fn [tut-no page]
                                           (generate-xml tut-no page {:shuffle? false}))
                                         %)))))

(def content (make-content [t-ac/content
                            t-0/content t-k/content
                            t-l/content
                            t-s2/content
                            t-s/content t-s3/content
                            t-ax/content
                            t-fizz/content])

(def tutorials (:tutorials content))
(def chaps (:chaps content))
(def chapnames (:chapnames content))

(when dev
  ;;(fsa/connect)
  ;;(print (tst/test-pure))
  (if-not (= (count tutorials) (reduce + chaps))
    (print "Sum of chaps not number tutorials")
    (print "sum chaps ok"))
  (if-not (= (count chaps) (count chapnames))
    (print "count chaps not count chapnames")
    (print "count chaps ok")))

(defn page->chapter [page-no]
  (- (count chaps) (count (filter #(> % page-no) (reductions + chaps)))))

(defn chapter->page [chap-no]
  (if (pos? chap-no) (nth (reductions + chaps) (dec chap-no)) 0))

(defonce state (rc/atom nil))

(defn set-state-field [kw value]
  (swap! state assoc kw value))

(defn update-state-field [kw fun]
  (swap! state update kw fun))

(defn load-workspace [xml-text]
  (try (.. blockly/Xml
           (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-text))
                                         (.getMainWorkspace blockly)))
       (catch js/Error e (set-state-field :stdout [(str (.-message e))]))))

(defn append-to-workspace [xml-text]
  (try (.. blockly/Xml
           (appendDomToWorkspace (.. blockly/Xml (textToDom xml-text))
                                 (.getMainWorkspace blockly)))
       (catch js/Error e (set-state-field :stdout [(str (.-message e))]))))

;;(when dev (fsa/trace-ref state))

(defn make-first-chapters [the-chaps]
  (conj (butlast (reductions + the-chaps)) 0))

(def first-of-chapters (make-first-chapters chaps))

(defn make-data-store [the-firsts]
  (let [last-of-chapters (into #{} (map dec the-firsts))]
    (atom {:saved-workspace-xml nil
           :solved-tutorials
           (if dev
             ;;(into #{} (range -1 300)) ;;to unlock all solutions ;;klm
             last-of-chapters
             last-of-chapters)
           :sci-env (atom nil)})))

(defonce data-store (make-data-store first-of-chapters))

(defn get-data-store-field [kw]
  (get @data-store kw))

(defn set-data-store-field [kw value]
  (swap! data-store assoc kw value))

(defn update-data-store-field [kw fun]
  (swap! data-store update kw fun))

(defn get-saved-workspace-xml [key]
  (get-in @data-store [:saved-workspace-xml key]))

(defn set-saved-workspace-xml [data]
  (swap! data-store assoc :saved-workspace-xml data))

(defn update-saved-workspace-xml [fun]
  (swap! data-store update :saved-workspace-xml fun))

(defn get-workspace-xml-str []
  (->> (.-mainWorkspace blockly)
       (.workspaceToDom blockly/Xml)
       (.domToPrettyText blockly/Xml)))

(defn swap-workspace []
  (if (= (:solution-no @state) -1)
    (do
      (update-saved-workspace-xml #(assoc % :xml-code (get-workspace-xml-str)))
      (swap! state assoc :solution-no 0)
      (load-workspace (get-saved-workspace-xml :xml-solution)))
    (do
      (update-saved-workspace-xml #(assoc % :xml-solution (get-workspace-xml-str)))
      (swap! state assoc :solution-no -1)
      (load-workspace (get-saved-workspace-xml :xml-code)))))

(def org-mode-tutorials
  (concat [["" "Select a tutorial" "dummy-slug"]] (js->clj ^js js/external_tutorials)))

(defn reset-state [tutorial-no]
  (let [tn (:tutorial-no @state)
        ds (:desc @state)
        sn (:solution-no @state)
        ac (:accepted? @state)
        init {:stdout []
              :inspect []
              :sci-error nil
              :sci-error-full nil
              :result-raw nil
              :code nil
              :edn-code nil
              :edn-code-orig nil
              :modal-style-display "none"
              :run-button true
              :tutorial-no tn
              :accepted? ac
              :desc ds
              :solution-no sn
              :colored-code false
              :forward-button-green false
              :url-select (ffirst org-mode-tutorials)}
        check (or (not @state) (= (into (hash-set) (keys init))
                                  (into (hash-set) (keys @state))))]
    (reset! state (merge init
                         (when tutorial-no
                           (set-saved-workspace-xml {:xml-code (:xml-code (nth tutorials tutorial-no))
                                                     :xml-solution (:xml-solution (nth tutorials tutorial-no))})
                           {:tutorial-no tutorial-no
                            :desc (:description (nth tutorials tutorial-no))
                            :solution-no -1 ;; -1 means "Puzzle" loaded in workspace
                            :accepted? false})))

    (when tutorial-no
      (when (and (:xml-solution (nth tutorials tutorial-no))
                 (contains? (get-data-store-field :solved-tutorials) (dec tutorial-no)))
        (swap-workspace)))

    (when-not check
      (swap! state assoc :stdout [(str "state is not in best state, pls. report. " (keys @state))]))))

(defonce app-state (rc/atom nil))

(defn set-scrollbar [x y]
  (when x
    (.. blockly -mainWorkspace (scroll x y))))

(defn goto-page! [page-no]
  (load-workspace (:xml-code (nth tutorials page-no)))
  (apply set-scrollbar (:scroll (nth tutorials page-no)))
  (gforms/setValue (gdom/getElement "tutorial_no") page-no)
  (reset-state page-no)
  (reset! app-state 0)
  page-no)

(defn tex-comp [txt]
  [:div {:ref (fn [el] (try (.Queue js/MathJax.Hub
                                    #js ["Typeset" (.-Hub js/MathJax) el])
                            (catch js/Error e (println (.-message e)))))}
   txt])

(defn html-tex-comp [e]
  [tex-comp (sicm/tex e)])

(def reagent-component-bindings
  {'->tex-equation html-tex-comp
   'tex html-tex-comp
   'verse-rotate cmpnts/html-verse-rotate-comp
   'verse-fade-in cmpnts/html-verse-fade-in-comp
   'verse-fade-out cmpnts/html-verse-fade-out-comp})

(defn start-timer [fu ms max msg]
  (let [timer (atom nil)
        counter (atom 0)
        stop-timer (fn [msg]
                     (js/clearInterval @timer)
                     msg)
        step (fn []
               (swap! counter inc)
               (if (< @counter max) (fu nil) (stop-timer nil)))]
    (reset! timer (js/setInterval step ms))
    msg))

(def bindings-all
  (let [new-println
        (fn [& x] (swap! state #(update % :stdout conj (apply str x))) nil)
        tex-inspect (fn [x] (swap! state #(update % :inspect conj x)) x)]
    (merge
     (es/namespaces 'sicmutils.numerical.minimize)
     (es/namespaces 'sicmutils.mechanics.lagrange)
     (es/namespaces 'sicmutils.env)
     (es/namespaces 'sicmutils.abstract.function) ;;needs to be after sicmutils.env
     sicm/bindings
     reagent-component-bindings
     {'println new-println
      'html-tex html-tex-comp
      workspace!/inspect-fn-sym tex-inspect
      'app-state app-state
      'start-timer start-timer})))

(defn set-sci-environment []
  (sci/eval-string "(identity 0)"
                   {:bindings bindings-all
                    :namespaces es/namespaces
                    :env (get-data-store-field :sci-env)}))

(defn reset-tutorials! [content]
  (let [foc (make-first-chapters (:chaps content))]
    (set! tutorials (:tutorials content))
    (set! chaps (:chaps content))
    (set! chapnames (:chapnames content))
    (set! first-of-chapters foc)
    (set! data-store (make-data-store foc))
    (set-sci-environment)))

(defn goto-lable-page!-1 [lable]
  (let [cnt (count (take-while false? (map #(= lable (:lable %)) tutorials)))]
    (when (< cnt (count tutorials)) (goto-page! cnt))))

(defn goto-lable-page! [lable swap-state-fn]
  (let [cnt (count (take-while false? (map #(= lable (:lable %)) tutorials)))]
    (when (< cnt (count tutorials)) (goto-page! cnt))
    (when swap-state-fn
      (swap! state swap-state-fn))))

(defn tutorial-fu [inc-or-dec]
  (fn []
    (let [el (gdom/getElement "tutorial_no")
          idx-old  (gstring/toNumber (gforms/getValue el))
          idx-new (inc-or-dec idx-old)
          idx (cond
                (< -1 idx-new (count tutorials)) idx-new
                (> 0 idx-new) 0
                (< (dec (count tutorials)) idx-new) (dec (count tutorials))
                (< -1 idx-old (count tutorials)) idx-old
                :else 0)]
      (goto-page! idx))))

(def output-width utils/output-width)

(defn part-str [width s]
  (apply str
         (interpose "\n"
                    (map (partial apply str)
                         (partition-all width s)))))

(defn augment-code-fu [edn-code flat-code fn-code]
  (if (seq (filter #{(second fn-code)} flat-code)) ;;consider use of function "some"
    (into [] (cons fn-code edn-code))
    edn-code))

(defn start-with-div? [last-edn-code]
  (and (vector? last-edn-code)
       (= ":div" (subs (str (first last-edn-code)) 0 4))))

(defn start-with-tex-equation? [last-edn-code]
  (and (list? last-edn-code)
       (= '->tex-equation (first last-edn-code))))

(defn parse-:div> [e]
  (let [sf (str (first e))
        sb (symbol (subs sf 5))]
    (cond-> e
      (and (= ":div>" (subs sf 0 5)) (get reagent-component-bindings sb))
      (assoc 0 sb))))

(defn augment-code-div [edn-code inspect-fn]
  (let [l (last edn-code)]
    (if (and (not inspect-fn) (start-with-div? l))
      (conj (into [] (butlast edn-code))
            (list 'defn 'cljtiles-reagent-component
                  "added by clj-tiles parser"
                  []
                  (parse-:div> l))
            'cljtiles-reagent-component)
      edn-code)))

(defn augment-code-tex-equation [edn-code inspect-fn]
  (let [l (last edn-code)]
    (if (and (not inspect-fn) (start-with-tex-equation? l))
      (conj (into [] (butlast edn-code))
            (list 'defn 'cljtiles-reagent-component
                  "added by clj-tiles parser"
                  []
                  (into [] l))
            'cljtiles-reagent-component)
      edn-code)))

(defn augment-code [edn-code inspect-fn {:keys [solved? from-startsci?]}]
  (let [s1 "(L-free-particle 'm)"
        s2 "(L-free-particle m)"
        s3 "(L-free-particle 'mass)"
        s4 "(L-free-particle mass)"
        flat-code
        (->> edn-code
             (w/postwalk #(if (and (list? %) (= s1 (str %))) s1 %))
             (w/postwalk #(if (and (list? %) (= s2 (str %))) s2 %))
             (w/postwalk #(if (and (list? %) (= s3 (str %))) s3 %))
             (w/postwalk #(if (and (list? %) (= s4 (str %))) s4 %))
             (w/postwalk #(if (map? %) (vec %) %))
             flatten)]
    (-> edn-code
        (augment-code-fu flat-code
                         '(defn vec-rest "added by clj-tiles parser" [x]
                            (let [r (rest x)] (if (seq? r) (vec r) r))))
        (augment-code-fu flat-code
                         '(defn vec-cons "added by clj-tiles parser" [x coll]
                            (let [c (cons x coll)] (if (seq? c) (vec c) c))))
        #_(augment-code-fu flat-code
                           '(defn L-free-particle "added by clj-tiles parser" [x]
                              (comp sicmutils-double (L-free-particle-sicm x))))
        ;; above line changed for next line, see sicm issue #271; not needed since sicmutils 0.21.1
        #_(augment-code-fu flat-code
                           '(defn Lagrangian-action
                              "added by clj-tiles parser"
                              [& opts]
                              (apply Lagrangian-action-sicm (concat opts [{:compile? true}]))))
        (augment-code-fu flat-code
                         '(def Lagrangian-signature '(-> (UP Real Real Real) Real)))
        (augment-code-fu flat-code
                         '(defn printlns [& lines] (run! println lines)))
        (as-> $ (if (and (:accepted? @state)
                         (not (and from-startsci? solved?)))
                  (identity $)
                  (-> $
                      (augment-code-div inspect-fn)
                      (augment-code-tex-equation inspect-fn)))))))

(defn get-inspect-form [edn-code]
  (ca/inspect-form edn-code workspace!/inspect-fn-sym))

;; this executes every form one by one, no matter whether an error occurs
;; lets inspect expressions after an error
;; would also appear to have the advantage of showing the last valid result
;; if there were not  1) this stack overflow problem
;; and 2) experience shows, that it is confusing anyway to show some result
;; along with an error after pressing the run button.
;; So: the thing is very useful for inspect, so it is used there
;; and there only the errors are important, no results needed.
(defn cljtiles-eval-one-by-one [edn-code {:keys [sci-env] :as context}]
  (let [the-errs (atom [])
        the-env (atom @sci-env)
        sci-eval (fn [code-str env errs]
                   (try (sci/eval-string code-str {:env env})
                        (catch js/Error e (swap! errs conj e) :sci-error)))
        cbr (utils/code-break-primitive edn-code)
        _results (doall (map #(sci-eval % the-env the-errs) cbr))
        err-msgs (map #(.-message %) @the-errs)
        ;; cbr-noflines (map (fn [y] (inc (count (filter (fn [x] (= "\n" x)) y)))) cbr)
        ;; cbr-sumlines (reductions + (cons 0 cbr-noflines))
        ;; err-lines (map #(.-data %) @the-errs)
        ;;first-err-num (count (take-while #(not= % :sci-error) results))
        ;;!!!! this produces a stack overflow as comaring to sicm expressions seems to be very expensive !!!
        ;;err-correct-line (update (first err-lines) :line #(+ % (nth cbr-sumlines first-err-num)))
        ;; takes last good result
        ;;good-results (drop-while #(= % :sci-error) (reverse results))
        ;;good-result (first good-results)

        ;; takes result before first error
        ;;good-results (take-while #(not= % :sci-error) results)
        ;;good-result (last good-results)
        erg {:result nil #_{:expression good-result
                            :number (dec (count good-results))
                            :line (inc (nth cbr-sumlines (dec (count good-results)) -1))}
             :err {:message (first err-msgs)
                   :line nil ;;(:line err-correct-line)
                   :column nil ;;(:column err-correct-line)
                   :err-msgs err-msgs}
             :str-expressions cbr
             :str-code (apply str (interpose "\n" cbr))}]
    (if-not (or (seq (:inspect @state)) (get-in erg [:err :message]) (:recur context))
      (cljtiles-eval-one-by-one [(get-inspect-form edn-code)] (assoc context :recur true))
      erg)))

(defn cljtiles-eval [code-break-str {:keys [sci-env]}]
  (let [the-err-msg (atom nil)
        erg (try (sci/eval-string code-break-str {:env sci-env})
                 (catch js/Error e
                   (reset! the-err-msg {:message (.-message e)
                                        :line (:line (.-data e))
                                        :column (:column (.-data e))})
                   nil))]
    {:result {:expression erg}
     :err @the-err-msg
     :str-code code-break-str}))

(defn get-edn-code [xml-str inspect-id inspect-fn]
  (let [edn-xml (sax/xml->clj xml-str)
        eci (edn->code/parse edn-xml {:id inspect-id :fun inspect-fn})
        inspect-fn-name (when inspect-fn (first (inspect-fn 0)))]
    (ca/prepare-fns eci inspect-fn-name)))

(defn gen-code [{:keys [inspect-fn] :as context}]
  (let [xml-str (get-workspace-xml-str)
        edn-code (get-edn-code xml-str
                               (some-> (get context "block") (.-id))
                               inspect-fn)
        aug-edn-code (augment-code edn-code inspect-fn context)
        str-code (utils/code->break-str aug-edn-code)]
    (update-state-field :result-raw (fnil identity "nothing calculated"))
    (swap! state merge
           {:code str-code ;;sometimes overridden by startsci + tex-inspect
            :edn-code aug-edn-code
            :edn-code-orig edn-code})))

(defn ^:export startsci [context]
  (reset-state nil)
  (let [{:keys [edn-code]} (gen-code (assoc context :from-startsci? true))
        sci-env (get-data-store-field :sci-env)
        run-code (fn [edn-code {:keys [inspect-fn] :as context}]
                   (let [cbr (utils/code->break-str edn-code)]
                     (if inspect-fn
                       (cljtiles-eval-one-by-one edn-code context)
                       (cljtiles-eval cbr context))))
        erg (run-code edn-code (assoc context :sci-env (atom @sci-env)))]
    (swap! state merge
           (let [{:keys [result err str-code]} erg]
             {:code str-code
              :result-raw (:expression result)
              :sci-error (:message err)
              :sci-error-full err}))))

(defn run-raw-code [code-str]
  (let [erg (cljtiles-eval code-str {:sci-env (get-data-store-field :sci-env)})]
    (if (:err erg)
      (println "code execution error")
      (println "code executed sucessfully"))
    (.log js/console erg)))

(defn init-url [url]
  (let [chapname (re-find #"[ \w-]+?(?=\.)" (guri/getPath url))]
    (goto-page! 1) ;;to make the next (goto-page! 0) trigger a re-render
    (-> (js/fetch url)
        (.then #(.text %))
        (.then (fn [txt]
                 (reset-tutorials!
                  (make-content
                   [(t-adv1/generate-content txt chapname) t-ac/content]))
                 (goto-page! 0))))))

(defn open-modal []
  (swap! state assoc :modal-style-display "block"))

(defn modal-comp [_]
  (let [textarea-element (atom nil)
        parse
        (fn [e]
          (let [a (edn/read-string (str "[" e "]"))
                b (first a)]
            (if (and (map? b) (:code b))
              (:xml-code (generate-xml nil b {}))
              (map #(rep-amp (gb/rpg [] %)) a))))
        run-parser
        (fn []
          (let [txt (.-value @textarea-element)]
            (if (some #(string/starts-with? txt %) ["http" "file"])
              (init-url txt)
              (let [xml (parse txt)]
                (if (seq? xml)
                  (run! (fn [c] (append-to-workspace c)) xml)
                  (load-workspace xml))))))
        close-modal
        (fn []
          (set! (.-value @textarea-element) "")
          (set-state-field :url-select (ffirst org-mode-tutorials))
          (swap! state assoc :modal-style-display "none"))]
    (fn [{:keys [tutorial-no modal-style-display url-select]}]
      [:div {:id "myModal", :class "modal"
             :style {:display modal-style-display}}
       [:div {:class "modal-content"}
        [:span
         [:select {:value url-select
                   :on-change (fn [el]
                                (let [v (.. el -target -value)]
                                  (set-state-field :url-select v)
                                  (set! (.-value @textarea-element) v)))}
          (map-indexed (fn [idx [val txt]] [:option {:key idx :value val} txt]) org-mode-tutorials)]
         " made by the community, or type a Clojure expression:"]
        [:p]
        [:textarea {:cols 80 :rows 10
                    :ref (fn [e] (reset! textarea-element e) (some-> e .focus))}]
        [:p]
        [:button {:on-click #(do (run-parser) (close-modal))}
         "Insert"]
        " "
        (when-let [h (:hint (nth tutorials tutorial-no))]
          (let [i (atom 0)]
            [:button {:on-click (fn []
                                  (set! (.-value @textarea-element)
                                        (get h @i))
                                  (swap! i inc))}
             "Hint"]))
        [:button {:style {:float "right"} :on-click close-modal}
         "Cancel"]
        [:button {:style {:float "right"}
                  :on-click (fn []
                              (set! (.-value @textarea-element)
                                    "\"For more information go to:\"\n\"https://github.com/kloimhardt/clj-tiles\""))}
         "About"]]])))

(defn desc-button []
  [:button {:on-click #(reset-state nil)} "Clear output"])

(defn radios []
  [:<>
   (-> (fn [[idx name]]
         ^{:key name}
         [:label {:style {:font-size "80%" :font-family "courier"}}
          [:input {:type :radio :name "solution-radio"
                   :on-change #(do (reset-state nil) (swap-workspace))
                   :checked (= idx (:solution-no @state))}]

          name])
       (map [[-1 "Puzzle"] [0 "Solution"]])
       doall)])

(defn chapter-range [n]
  (range (apply max (take-while (partial >= n) first-of-chapters)) (inc n)))

(defn solved? [tutorial-no]
  (when-let [xml-sol (:xml-solution (nth tutorials tutorial-no))]
    (let [edn-sol (utils/get-edn-code-simpl xml-sol)
          edn-puzz (utils/get-edn-code-simpl (get-workspace-xml-str))]
       (= edn-sol edn-puzz))))

(defn set-forward-button-green [solution-no]
  (if (= -1 solution-no)
      (set-state-field :forward-button-green true)
      (set-state-field :forward-button-green false)))

(defn run-button-comp [tutorial-no solution-no]
  [:button {:on-click (fn []
                        (let [sld (solved? tutorial-no)]
                          (startsci {:solved? sld})
                          (when sld
                            (set-forward-button-green solution-no))))}

   "Run"])

(defn tutorials-comp [{:keys [run-button tutorial-no edn-code forward-button-green
                              solution-no accepted?]}]
  (when dev (prn "in tutorial-comp"))
  [:div
   [:span
    [:select {:value (page->chapter tutorial-no)
              :on-change (fn [el]
                           (let [chap (gstring/toNumber
                                       (.. el -target -value))
                                 no (chapter->page chap)]
                             (goto-page! no)))}
     (map-indexed (fn [idx val] [:option {:key idx :value idx} val]) chapnames)]
    " "
    [:button {:on-click (fn [_]
                          ((tutorial-fu dec))
                          (when-not (contains? (get-data-store-field :solved-tutorials) (dec (:tutorial-no @state)))
                            (set-state-field :accepted? true)
                            (gen-code nil)))}
     "<"]
    " "
    [:input {:read-only true :size (inc (* 2 (count (str (count tutorials)))))
             :value (str (inc tutorial-no) "/" (count tutorials))}]
    " "
    [:button {:on-click (fn []
                          (when forward-button-green
                            (let [new-tutnos-unlocked
                                  (->> (chapter-range tutorial-no)
                                       (remove (get-data-store-field :solved-tutorials)))
                                  new-tuts-unlocked
                                  (->> new-tutnos-unlocked
                                       (map #(nth tutorials %))
                                       (filter :xml-solution))]
                              (run! (fn [tut]
                                      (->> (:shadow tut)
                                           (map #(if-let [t (some-> % (string/trim))]
                                                   (if (string/starts-with? t "(ns")
                                                     (str "(require '[sicmutils.env :as e]) #_(" (subs t 4))
                                                     %)
                                                   %))
                                           (run! run-raw-code))
                                      (run-raw-code (:src tut)))
                                    new-tuts-unlocked)
                              (update-data-store-field :solved-tutorials
                                                       #(apply conj % new-tutnos-unlocked))))
                          ((tutorial-fu inc))
                          (when-not (contains? (get-data-store-field :solved-tutorials) (dec (:tutorial-no @state)))
                            (set-state-field :accepted? true)
                            (gen-code nil)))
              :style (when forward-button-green {:color "white"
                                                 :background-color "green"})}
     ">"]
    " "
    (when run-button
      (cond
        (get-inspect-form edn-code)
        [desc-button]
        (:xml-solution (nth tutorials tutorial-no))
        (if (contains? (get-data-store-field :solved-tutorials) (dec tutorial-no))
          [:span
           " "
           [run-button-comp tutorial-no solution-no]
           " "
           (when dev nil #_[radios])
           (if accepted?
             [:button {:on-click (fn []
                                   (load-workspace (:xml-code (generate-xml tutorial-no
                                                                            (nth tutorials tutorial-no)
                                                                            {:shuffle? true})))
                                   (gen-code nil))}
              "Shuffle"]
             [:button {:on-click (fn []
                                   (set-state-field :accepted? true)
                                   (when (not= -1 solution-no)
                                  ;; this when is a safety measure, should always trigger here
                                  ;; solution should always be loaded when this button appears
                                  ;; and we want to switch to the puzzle
                                     (swap-workspace))
                                   (gen-code nil))}
              "Get the Puzzle"])]
          [:p "A description will appear if the previous puzzle is solved. Maybe you want to go back. However, if you solve this puzzle, the green arrow will unlock the chapter up to the next page."])
        :else
        [run-button-comp tutorial-no solution-no]))]])

(defn my-str [x]
  (if (nil? x) "nil" (str x)))

(defn my-str-brk [e]
  (if (seq? e)
    (part-str output-width (apply str (interpose " " (map my-str e))))
    (part-str output-width (my-str e))))

(defn modify-error [msg]
  (if (= (subs msg 0 40)
         "Parameter declaration should be a vector")
    (str "Wrong Parameter declaration" (subs msg 40))
    msg))

(defn error-comp-inspect [{:keys [sci-error-full code]} ifo]
  (let [flex50 {:style {:flex "50%"}}]
    [:div {:style {:display "flex"}}
     [:div flex50
      [:h3 "Code interpretation result"]
      (if (> (count (:err-msgs sci-error-full)) 1)
        [:<>
         [:pre (my-str-brk (str "The expression " ifo " could not be displayed due to one of the following reasons:"))]
         (map-indexed (fn [idx msg] ^{:key idx} [:pre (my-str-brk (str (inc idx) ". " (modify-error msg)))])
                      (:err-msgs sci-error-full))]
        [:<>
         [:pre (my-str-brk (str "The expression " ifo " could not be displayed  because"))]
         [:pre (str (my-str-brk (modify-error (first (:err-msgs sci-error-full)))))]])]
     [:div flex50
      [utils/render-colored code nil nil false nil nil nil nil]]]))

(defn extended-gen-code [tutorial-no solution-no]
  (fn [x]
    (gen-code x)
    (when (solved? tutorial-no)
      (set-forward-button-green solution-no))))

(defn error-comp [{:keys [sci-error-full sci-error code
                          edn-code tutorial-no solution-no colored-code]}]
  (let [flex50 {:style {:flex "50%"}}]
    [:div {:style {:display "flex"}}
     [:div flex50
      [:h3 "Code interpretation result"]
      [:pre (my-str-brk (modify-error sci-error))]
      [:pre (str "line " (:line sci-error-full) " column " (:column sci-error-full))]]
     [:div flex50
      [utils/render-colored code edn-code
       (:xml-solution (nth tutorials tutorial-no))
       colored-code tutorial-no
       (get-data-store-field :solved-tutorials)
       update-state-field
       (extended-gen-code tutorial-no solution-no)]]]))

(defn result-comp [{:keys [result-raw edn-code edn-code-orig code
                           tutorial-no solution-no colored-code]}]
  (if (= edn-code edn-code-orig :showcode) ;;never true, remove :showcode to supress code display.
    [:pre (my-str result-raw)]
    (let [flex50 {:style {:flex "50%"}}
          tut (nth tutorials tutorial-no)]
      [:div {:style {:display "flex"}}
       [:div flex50
        [:h3 "Result"]
        [:pre (my-str-brk result-raw)]]
       (when-not (:no-code-display tut)
         [:div flex50
          [utils/render-colored code edn-code
           (:xml-solution tut)
           colored-code tutorial-no
           (get-data-store-field :solved-tutorials)
           update-state-field
           (extended-gen-code tutorial-no solution-no)]])])))

(defn output-comp [{:keys [edn-code tutorial-no inspect sci-error stdout
                           desc result-raw code accepted?]
                    :as the-state}]
  (let [tut (nth tutorials tutorial-no)]
    (if-let [ifo (get-inspect-form edn-code)]
      [:<>
       (cond
         (seq inspect)
         [:<>
          (map-indexed (fn [idx v]
                         ^{:key idx} [:<>
                                      [tex-comp (sicm/kind? v)]
                                      [:hr]])
                       inspect)
          (when-let [msg-fn (:message-fn tut)]
            [tex-comp (msg-fn the-state ifo goto-lable-page!)])]
         sci-error
         [:<>
          (when-let [error-msg-fn (:error-message-fn tut)]
            [:p (error-msg-fn the-state ifo (:message-fn tut))])
          [error-comp-inspect the-state (last ifo)]]
         (= (last ifo) :start-interactive)
         [tex-comp ((:message-fn tut) the-state ifo goto-lable-page!)]
         :else (str "Expression " (last ifo) " was never called."))]
      (let [custom-comp (and (:comp-fn tut) ((:comp-fn tut) the-state))]
        [:<>
         (when-not custom-comp
           (map-indexed (fn [idx v]
                          ^{:key idx} [:pre v])
                        stdout))
         (cond
           sci-error
           [:<>
            [error-comp the-state]
            (when desc [desc-button])]
           code
           [:<>
            (cond
              custom-comp
              [custom-comp]
              (= 'cljtiles-reagent-component (last edn-code))
              [result-raw]
              :else
              [result-comp the-state])
            (when (and desc (not accepted?)) [:p [desc-button]])]
           desc
           [:div {:style {:column-count 2}}
            [tex-comp desc]])]))))

(defn theview []
  [:div
   [modal-comp @state]
   [tutorials-comp @state]
   [utils/error-boundary
    [output-comp @state]
    reset-state
    set-state-field :stdout ["Something went wrong rendering the result"]]])

(defn some-development-stuff []
  ;;((tutorial-fu identity)) ;;load currenet workspace new !!:free-particle dose not work as a consequence!!
  (let [url
        "https://raw.githubusercontent.com/mentat-collective/fdg-book/main/clojure/org/chapter001.org"]
    (init-url url)
    )
  :end)

(defn ^{:dev/after-load true} render []
  (when dev (some-development-stuff))
  (rd/render [theview] (gdom/getElement "out")))

(defn ^{:export true} output []
  (workspace!/init startsci open-modal)
  (goto-page! (dec 1))
  (set-sci-environment)
  (when-let [u (some-> (not-empty (.. js/window -location -search))
                       js/URLSearchParams.)]
    (let [urls (into {} (map (fn [[url _label slug]] [slug url])) org-mode-tutorials)
          p (.get u "page")
          o (.get u "org")]
      (cond
        o
        (init-url o)
        (get urls p)
        (init-url (get urls p))
        (= p "freeparticle")
        (goto-lable-page! :free-particle
                          #(assoc % :run-button false :edn-code (list workspace!/inspect-fn-sym :start-interactive)))
        (= p "higherorder")
        (goto-lable-page! :higher-order nil)
        (= p "pendulumfinal")
        (goto-lable-page! :pendulum-final nil)
        :else
        (-> p
            js/parseInt
            dec
            goto-page!))))
  (render))

(comment

(defn tuts-namespace-fn []
  (some-> (some->> tutorials first :shadow first (re-find #"\(ns .+"))
          (subs 4)))

(defn add-ns-edn [edn-code]
  (if-let [ns (get-data-store-field :tuts-ns)]
    (if (and edn-code (seq edn-code))
      (into []
            (concat
              [(edn/read-string (str "(in-ns '" ns ")"))]
              edn-code))
      edn-code)
    edn-code))

(defn add-ns-txt [txt]
  (if-let [ns (get-data-store-field :tuts-ns)]
    (if (and txt
             (seq txt)
             (not (string/starts-with? (string/trim txt) (str "(ns " ns))))
      (str "(in-ns '" ns ") " txt)
      txt)
    txt))


  :end)
