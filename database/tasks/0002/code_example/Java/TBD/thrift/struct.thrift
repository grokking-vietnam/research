namespace java com.pmt.thrift.tstruct

typedef string KType
//typedef binary VType

struct TDBResult {
  1: required i32 err,
  2: optional binary val,
}

struct PingResult {
  1: required i32 err,
  2: optional i32 val,
}

struct TDBResearch {
  1: required string task,
  2: required string leader,
  3: optional list<string> member,
}