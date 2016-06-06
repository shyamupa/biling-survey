Data and scripts for reproducing the results from the ACL 2016 paper.


## Running Monolingual Evaluation

### Running QVec

Simply run
```
python qvec_cca2.py --in_vectors ~/mydir/en1.vectors
```

### Running Word Similarity with Steigler's p-value
Run the following over a pair of models for which you want to compute whether the difference is significant,
```
python stat_signf.py ~/mydir/en1.vectors ~/mydir/vulic_vectors/en2.vectors data/word-sim/EN-SIMLEX-999.txt
```

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
(1.376206182800332, 0.014533569095982752)
```
Where the p-value is 0.014.

## Running CLDC

Download the code

You will also need to procure the RCV2 Multilingual Corpus.

## Running CLDEP

You will need to get the universal dependencies treebank v1.2 from [here](http://universaldependencies.org/).

You will also need to install the parser released [here](https://github.com/jiangfeng1124/acl15-clnndep).

An example training script is provided under the file `example.sh`. It trains on english and german treebanks using the embeddings trained by a particular model. It uses the config file provided under `config/config.cfg`. Note that you may need to change the embedding dimensions `embedding_size` for your embeddings.


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