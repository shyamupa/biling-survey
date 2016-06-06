#!/usr/bin/env bash

MYDIR=/shared/bronte/upadhya3/europarl/mfaruqui # where all vectors reside

time ./bin/clnndep -train udt/de/de-universal-train.conll \
     -dev udt/de/de-universal-dev.conll \
     -model bicvm_en-de.de.model \
     -emb $MYDIR/bicvm_vectors/bicvm.en-de.de.200 \
     -cfg conf/config.cfg

time ./bin/clnndep -train udt/en/en-universal-train-brown.conll \
     -dev udt/en/en-universal-dev-brown.conll \
     -model bicvm_en-de.en.model \
     -emb $MYDIR/bicvm_vectors/bicvm.en-de.en.200 \
     -cfg conf/config.cfg


if [[ $? == 0 ]]        # success
then
    :                   # do nothing
else                    # something went wrong
    echo "SOME PROBLEM OCCURED";
fi
