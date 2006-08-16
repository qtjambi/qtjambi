
#include "type_environment.h"
#include "type_ctors.h"

int main()
{
  using namespace rpp;

  TypeEnvironment tau;

  // int *x
  TypeVariable tv1 = tau.newTypeVariable();
  tv1.addPointerTypeCtor();
  tv1.addPrimitiveTypeCtor("int");

  // int *y
  TypeVariable tv2 = tau.newTypeVariable();
  tv2.addPointerTypeCtor();
  tv2.addPrimitiveTypeCtor("int");

  // int *x[10]
  TypeVariable tv3 = tau.newTypeVariable();
  tv3.addArrayTypeCtor(10);
  tv3.addPointerTypeCtor();
  tv3.addPrimitiveTypeCtor("int");

  Q_ASSERT(tv1 == tv2);

  // Cool::Iterator x;
  TypeVariable tv4 = tau.newTypeVariable();
  tv4.addMemberTypeCtor("Iterator");
  tv4.addNamedTypeCtor("Cool");

  // const Cool::Iterator *x;
  TypeVariable tv5 = tau.newTypeVariable();
  tv4.addConstTypeCtor();
  tv4.addPointerTypeCtor();
  tv4.addMemberTypeCtor("Iterator");
  tv4.addNamedTypeCtor("Cool");
}

// kate: indent-width 2;
