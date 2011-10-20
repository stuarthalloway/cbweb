(ns cbweb.codebreaker
  (:use clojure.pprint)
  (:require [clojure.data :as data]
            [clojure.math.combinatorics :as comb]
            [clojure.java.io :as io]))

;; step 1 use clojure.data

;; step 2 repl test
#_(data/diff [:r :g :g :b] [:r :y :y :b])

;; step 3
(defn exact-matches
  [c1 c2]
  (let [[_ _ matches] (data/diff c1 c2)]
    matches))

;; step 4 repl test exact-matches
#_(exact-matches [:r :g :g :b] [:r :y :y :b])

;; now let's compare regardless of position, using
;; [:r :g :g :b] [:g :y :y :g]

;; step 5 repl test
;; (frequencies c1)
#_(frequencies [:r :g :g :b])

;; step 6 repl test
;; keep only the keys in the other collection
#_(select-keys (frequencies [:r :g :g :b]) [:y :y :y :g])

;; step 7 repl test
#_(def c1 [:r :g :g :b])
#_(def c2 [:y :y :y :g])
#_(let [f1 (select-keys (frequencies c1) c2)
        f2 (select-keys (frequencies c2) c1)]
  (merge-with min f1 f2))

;; step 8
(defn unordered-matches
  [c1 c2]
  (let [f1 (select-keys (frequencies c1) c2)
        f2 (select-keys (frequencies c2) c1)]
    (merge-with min f1 f2)))

;; steps 9,10,11: exact, unordered, score
(defn score
  [c1 c2]
  (let [exact (count (remove nil? (exact-matches c1 c2)))
        unordered (apply + (vals (unordered-matches c1 c2)))]
    {:exact exact :unordered (- unordered exact)}))

;; step 12
#_(score [:r :g :g :b] [:r :y :y :b])
#_(score c1 c2)


;; step 13 add combinatorics to project.clj

;; step 14 explore selections
;; (add pprint)
#_(pprint (comb/selections [:r :g :b :y] 2))

;; step 15 game and a guess are just two selections of games
#_(pprint (-> (comb/selections [:r :g :b :y] 2)
            (comb/selections 2)))


;; step 16 massage output for QA
(defn score-all-games
  "Given a number of colors and cols for a codebreaker game,
   return a map for every posssible game combination, including
   the secret, the guess, and the score"
  [colors columns]
  (map
   (fn [[secret guess]]
     {:secret (seq secret) :guess (seq guess) :score (score secret guess)})
   (-> (comb/selections colors columns)
       (comb/selections 2))))

;; seq around secret and guess above can be removed once pprint/print-table
;; is fixed to call (pr col) instead of (str col)
;; while in there, change table punctuation to match org-mode

;; step 17 score-table
#_(score-all-games [:R :G :B] 3)

;; step 18 could check either the clj or the tabular form of score-all-games
;; into source control and use it as a regression test
;; (add clojure.java.io)
#_(with-open [w (io/writer "scoring-table")]
    (binding [*out* w]
      (print-table (score-all-games [:R :G :B] 3))))
