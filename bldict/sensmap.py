# coding: utf-8
import sys
from collections import defaultdict
def load_sensemap(lines,vocab):
    sensemap={}
    for l in lines[1:]:
        # print l,len(l)
        if len(l)<3:
            continue
        if ' ' in l[2] or '-' in l[2]:
            continue
        l[2]=l[2].lower()
        if l[2] not in vocab:
            continue
        if l[0] in sensemap:
            sensemap[l[0]]+=[l[2]]
        else:
            sensemap[l[0]]=[l[2]]
    return sensemap

if __name__=="__main__":
    
    top=1000
    lcode = sys.argv[1] # 'de'
    code = sys.argv[2] # 'deu' 
    
    FRPATH='/shared/bronte/upadhya3/europarl/mfaruqui/uniq.en-'+lcode+'.'+lcode+'.vocab.min5'
    ENPATH='/shared/bronte/upadhya3/europarl/mfaruqui/uniq.en-'+lcode+'.en.vocab.min5'
    
    frwords=[l.split() for l in open(FRPATH)]
    enwords=[l.split() for l in open(ENPATH)]

    frold,enold=len(frwords),len(enwords)
    # print 'fr vocab before prune',len(frwords),'en vocab before prune',len(enwords)
    
    
    frwords = dict([i for i in frwords if len(i)==2  and int(i[1])>top])
    enwords = dict([i for i in enwords if len(i)==2 and int(i[1])>top])

    enlines=[l.strip().split('\t') for l in open('/home/upadhya3/Desktop/data/wikt/wn-wikt-eng.tab')]
    frlines=[l.strip().split('\t') for l in open('/home/upadhya3/Desktop/data/wikt/wn-wikt-'+code+'.tab')]

    en_senses=load_sensemap(enlines,enwords)
    fr_senses=load_sensemap(frlines,frwords)
    # print len(en_senses),len(fr_senses)

    overlap = [s for s in fr_senses if s in en_senses]
    
    for o in overlap:
        en_senses[o].sort(key=lambda x: int(enwords[x]), reverse=True)
        fr_senses[o].sort(key=lambda x: int(frwords[x]), reverse=True)
        print ' '.join(en_senses[o]),'\t',' '.join(fr_senses[o]) # emit dictionary
    # d = defaultdict(list)
    # for o in overlap:
    #     for e in en_senses[o]:
    #         for f in fr_senses[o]:
    #             d[e].append(f)
    # print len(d)
