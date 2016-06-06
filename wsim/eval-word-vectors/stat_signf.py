import sys
import os
import traceback
from read_write import read_word_vectors
from ranking import *
from corrstats import *

def compute_vs_gold(f,word_vecs):
    total_size=0
    not_found=0
    manual_dict, auto_dict = ({}, {})
    for line in f:
        line = line.strip().lower()
        word1, word2, val = line.split()
        if word1 in word_vecs and word2 in word_vecs:
            manual_dict[(word1, word2)] = float(val)
            auto_dict[(word1, word2)] = cosine_sim(word_vecs[word1], word_vecs[word2])
        else:
            not_found += 1
        total_size += 1
    return manual_dict,auto_dict,not_found,total_size

def compute_XY(f,word_vecs1,word_vecs2):
    total_size=0
    not_found=0
    auto_dict1, auto_dict2 = ({}, {})
    for line in f:
        line = line.strip().lower()
        word1, word2, val = line.split()
        if word1 in word_vecs1 and word2 in word_vecs1 and word1 in word_vecs2 and word2 in word_vecs2:
            auto_dict1[(word1, word2)] = cosine_sim(word_vecs1[word1], word_vecs1[word2])
            auto_dict2[(word1, word2)] = cosine_sim(word_vecs2[word1], word_vecs2[word2])
        else:
            not_found += 1
        total_size += 1
    return auto_dict1,auto_dict2,not_found,total_size

if __name__=="__main__":
    word_vec1_file = sys.argv[1]
    word_vec2_file = sys.argv[2]
    word_sim_file = sys.argv[3]
    word_vecs1 = read_word_vectors(word_vec1_file)
    word_vecs2 = read_word_vectors(word_vec2_file)
    print '================================================================================='
    print "%15s" % "Num Pairs", "%15s" % "Not found", "%15s" % "Rho"
    print '================================================================================='
    
    manual_dict,auto1_dict,not_found,total_size = compute_vs_gold(open(word_sim_file,'r'),word_vecs1)
    A=spearmans_rho(assign_ranks(manual_dict), assign_ranks(auto1_dict))
    print "%15s" % str(total_size), "%15s" % str(not_found),
    print "%15.4f" % A
    
    manual_dict,auto2_dict,not_found,total_size = compute_vs_gold(open(word_sim_file,'r'),word_vecs2)
    B=spearmans_rho(assign_ranks(manual_dict), assign_ranks(auto2_dict))
    print "%15s" % str(total_size), "%15s" % str(not_found),
    print "%15.4f" % B

    auto_dict1,auto_dict2,not_found,total_size = compute_XY(open(word_sim_file,'r'),word_vecs1,word_vecs2)
    C=spearmans_rho(assign_ranks(auto_dict1), assign_ranks(auto_dict2))
    print "%15s" % str(total_size), "%15s" % str(not_found),
    print "%15.4f" % C
    print dependent_corr(A, B, C, 999, method='steiger',twotailed=False)
