
#ifndef TYPE_ENVIRONMENT_H
#define TYPE_ENVIRONMENT_H

#include <QtCore/QSet>

#include "type_ctors.h"

namespace rpp {

class TypeEnvironment
{
public:
  TypeEnvironment();
  ~TypeEnvironment();

  TypeVariable newTypeVariable();

  const PointerTypeCtor *pointerTypeCtor();
  const ReferenceTypeCtor *referenceTypeCtor();
  const ConstTypeCtor *constTypeCtor();
  const VolatileTypeCtor *volatileTypeCtor();
  const PrimitiveTypeCtor *primitiveTypeCtor(const QString &name);
  const NamedTypeCtor *namedTypeCtor(const QString &name);
  const ArrayTypeCtor *arrayTypeCtor(int size);
  const MemberTypeCtor *memberTypeCtor(const QString &name);
  const FunctionTypeCtor *functionTypeCtor(const TypeVariable &returnType, const QList<TypeVariable> &arguments);
  const TemplateTypeCtor *templateTypeCtor(const QList<TypeVariable> &arguments);

private:
  // ### d-pointer
  PointerTypeCtor _M_pointerTypeCtor;
  ReferenceTypeCtor _M_referenceTypeCtor;
  ConstTypeCtor _M_constTypeCtor;
  VolatileTypeCtor _M_volatileTypeCtor;
  QSet<PrimitiveTypeCtor> _M_primitiveTypeCtors;
  QSet<NamedTypeCtor> _M_namedTypeCtors;
  QSet<ArrayTypeCtor> _M_arrayTypeCtors;
  QSet<MemberTypeCtor> _M_memberTypeCtors;
  QSet<FunctionTypeCtor> _M_functionTypeCtors;
  QSet<TemplateTypeCtor> _M_templateTypeCtors;

private:
  Q_DISABLE_COPY(TypeEnvironment)
};

} // namespace rpp

#endif // TYPE_ENVIRONMENT_H

// kate: indent-width 2;
