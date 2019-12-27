//
// Created by minhtam on 19/12/2019.
//

#ifndef NON_BLOCKING_DATA_H
#define NON_BLOCKING_DATA_H

#include <memory.h>

typedef struct {
    int a;
    int b;
    int operation;
} TCalculator;

int serializer(TCalculator *data, char *buff) {
    int meta = sizeof(TCaculator);
    memcpy(buff, &meta, sizeof(int));
    memcpy(buff + sizeof(int), data, sizeof(TCaculator));
    buff[sizeof(int) + sizeof(TCaculator)] = '\0';
    return sizeof(int) + sizeof(TCaculator) + 1;
}

TCalculator deserializer(char* buff) {
    int meta = sizeof(TCaculator);
    TCaculator ret;
    memcpy(&ret, &buff[sizeof(meta)], meta);
    return ret;
}

#endif //NON_BLOCKING_DATA_H
