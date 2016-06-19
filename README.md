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

## Running BLDict

The dictionaries used for evaluation are provided in `en.*.dict`.

The format is,

`en1 en2 <tab> fr1 fr2 fr3`

Where `en1` and `en2` are entries on the english side which all share `fr1, fr2, fr3` as possible translations. For eg.

```dangerous hazardous unsafe risky        dangereux dangereuse```

If you compute the english side tokens of the dictionary you should get 1510 (fr), 1425 (de), 1610(zh) and 1024(sv).

The evaluation code is provided under `my-evaluation`. First setup the dependencies using mvn compile and `mvn dependency:copy-dependencies`.
Then run,

```
sh run.sh evaluateBiDict de ~/mydir/bicvm_vectors/bicvm.en-de.en.200 ~/mydir/bicvm_vectors/bicvm.en-de.de.200
```

## Running CLDC

Download the code for the Klementiev et al. paper from [here](https://dl.dropboxusercontent.com/u/19557502/document-representations.tar.gz).

You will also need to procure the RCV2 Multilingual Corpus.

For `en --> L` (train on english and test on language L)

The english train split is same as one provided by Klementiev et al. Similarly for de's test split. The test splits for fr,sv,zh are in file `test-en-l2.txt` (There should be a distribution of 10 C, 300 E, 600 G, 900 M).

For `L --> en`
Train files for fr,sv,zh are in file `train-l2-en.txt`. For de, its same as before. The test files (en) are also same as before.


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