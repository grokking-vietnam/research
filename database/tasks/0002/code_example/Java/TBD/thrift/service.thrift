include "struct.thrift"
namespace java com.pmt.thrift.service

service BaseService {
  struct.PingResult ping()
}

service DataBaseService extends BaseService {

  /**
   * A method definition looks like C code. It has a return type, arguments,
   * and optionally a list of exceptions that it may throw. Note that argument
   * lists and exception lists are specified using the exact same syntax as
   * field lists in struct or exception definitions.
   */

   struct.TDBResult put(1:required struct.KType key, 2:required binary val),

   binary get(1:required struct.KType key),
}