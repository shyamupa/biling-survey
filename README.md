Data and scripts for reproducing the results from the ACL 2016 paper.


## Running Monolingual Evaluation

### Running QVec

### Running Word Similarity with Steigler's p-value
python stat_signf.py ~/mydir/en1.vectors ~/mydir/vulic_vectors/en2.vectors data/word-sim/EN-SIMLEX-999.txt
This will output something like,
```
Vectors read from: ~/mydir/en1.vectors
Vectors read from: ~/mydir/en2.vectors
==================================================
      Num Pairs       Not found             Rho
==================================================
         999              90          0.1234
         999              41          0.5678
         999             129          0.8429
(1.376206182800332, 0.084533569095982752)
```
Where the p-value is 0.084

## Running CLDC

## Running CLDEP




###Reference

```
@inproceedings{bicompare:16,
author = {Upadhyay, Shyam and Faruqui, Manaal and Dyer, Chris and Roth, Dan},
title = {Cross-lingual Models of Word Embeddings: An Empirical Comparison},
booktitle = {Proc. of ACL},
year = {2016},
url = {http://arxiv.org/abs/1604.00425}
}
```