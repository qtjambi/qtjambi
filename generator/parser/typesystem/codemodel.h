
#ifndef RPP_CODEMODEL_H
#define RPP_CODEMODEL_H

#include <QtCore/QList>

#include "type_ctors.h"

namespace rpp {

enum MemberKind {
  Kind_FileItem,
  Kind_NamespaceItem,
  Kind_ClassItem,
  Kind_UsingItem,
  Kind_FunctionItem,
  Kind_VariableItem,
  Kind_ArgumentItem,
};

class Member;
class FileItem;
class NamespaceItem;
class ClassItem;
class UsingItem;
class FunctionItem;
class VariableItem;
class ArgumentItem;

class CodeModel;
class MemberVisitor;

class MemberVisitor
{
public:
  MemberVisitor();
  virtual ~MemberVisitor();

  virtual bool preVisit(Member *) { return true; }
  virtual void postVisit(Member *) {}

  virtual void visit(FileItem *) {}
  virtual void visit(NamespaceItem *) {}
  virtual void visit(ClassItem *) {}
  virtual void visit(UsingItem *) {}
  virtual void visit(ArgumentItem *) {}
  virtual void visit(FunctionItem *) {}
  virtual void visit(VariableItem *) {}
};

class Member
{
public:
  Member(CodeModel *model, int kind);
  virtual ~Member();

  CodeModel *codeModel() const;
  bool isValid() const;

  Member *parent() const;
  void setParent(Member *parent);

  QString name() const;
  void setName(const QString &name);

  TypeVariable type() const;
  void setType(const TypeVariable &type);

  bool isFileItem() const;
  bool isNamespaceItem() const;
  bool isClassItem() const;
  bool isUsingItem() const;
  bool isFunctionItem() const;
  bool isVariableItem() const;

  const FileItem *toFileItem() const;
  const NamespaceItem *toNamespaceItem() const;
  const ClassItem *toClassItem() const;
  const UsingItem *toUsingItem() const;
  const FunctionItem *toFunctionItem() const;
  const VariableItem *toVariableItem() const;
  const ArgumentItem *toArgumentItem() const;

  FileItem *toFileItem();
  NamespaceItem *toNamespaceItem();
  ClassItem *toClassItem();
  UsingItem *toUsingItem();
  FunctionItem *toFunctionItem();
  VariableItem *toVariableItem();
  ArgumentItem *toArgumentItem();

  void accept(MemberVisitor *visitor);
  static void acceptMember(Member *member, MemberVisitor *visitor);

  virtual void accept0(MemberVisitor *visitor) = 0;
  virtual bool equalTo(const Member &other) const = 0;

private:
  CodeModel *_M_codeModel;
  int _M_kind;
  Member *_M_parent;
  QString _M_name;
  TypeVariable _M_type;
};

class MemberGroup: public Member
{
public:
  MemberGroup(int kind);
  virtual ~MemberGroup();

  int memberCount() const;
  int indexOfMember(Member *member) const;

  Member *memberAt(int index) const;
  void addMember(Member *member);

  QList<Member*>::Iterator firstMember();
  QList<Member*>::Iterator lastMember();

  QList<Member*>::ConstIterator firstMember() const;
  QList<Member*>::ConstIterator lastMember() const;

protected:
  QList<Member*> _M_members;
};

class NamespaceItem: public MemberGroup
{
public:
  NamespaceItem(int kind = Kind_NamespaceItem);
  virtual ~NamespaceItem();

  virtual void accept0(MemberVisitor *visitor);
  virtual bool equalTo(const Member &other) const;
};

class FileItem: public NamespaceItem
{
public:
  FileItem(int kind = Kind_FileItem);
  virtual ~FileItem();

  virtual void accept0(MemberVisitor *visitor);
  virtual bool equalTo(const Member &other) const;
};

class ClassItem: public MemberGroup
{
public:
  ClassItem(int kind = Kind_ClassItem);
  virtual ~ClassItem();

  virtual void accept0(MemberVisitor *visitor);
  virtual bool equalTo(const Member &other) const;
};

class UsingItem: public Member
{
public:
  UsingItem(int kind = Kind_UsingItem);
  virtual ~UsingItem();

  virtual void accept0(MemberVisitor *visitor);
  virtual bool equalTo(const Member &other) const;
};

class ArgumentItem: public Member
{
public:
  ArgumentItem(int kind = Kind_ArgumentItem);
  virtual ~ArgumentItem();

  QString defaultValue() const;
  void setDefaultValue(const QString &defaultValue);

  virtual void accept0(MemberVisitor *visitor);
  virtual bool equalTo(const Member &other) const;

private:
  QString _M_defaultValue;
};

class FunctionItem: public Member
{
public:
  FunctionItem(int kind = Kind_FunctionItem);
  virtual ~FunctionItem();

  QList<ArgumentItem*> arguments() const;
  int argumentCount() const;
  int indexOfArgument(ArgumentItem *argument) const;

  QList<ArgumentItem*>::ConstIterator firstArgument() const;
  QList<ArgumentItem*>::ConstIterator lastArgument() const;

  QList<ArgumentItem*>::Iterator firstArgument();
  QList<ArgumentItem*>::Iterator lastArgument();

  void clearArgument();
  void addArgument(ArgumentItem *argument);

  virtual void accept0(MemberVisitor *visitor);
  virtual bool equalTo(const Member &other) const;

private:
  QList<ArgumentItem*> _M_arguments;
};

class VariableItem: public Member
{
public:
  VariableItem(int kind = Kind_VariableItem);
  virtual ~VariableItem();

  virtual void accept0(MemberVisitor *visitor);
  virtual bool equalTo(const Member &other) const;
};

} // namespace rpp

#endif // RPP_CODEMODEL_H

// kate: indent-width 2
