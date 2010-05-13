conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     TABELA: BG_CONDICAO_PAGAMENTO_IDIOMA
--  ///////

delete from DEPEND_HEADER_ITEM_TMP;

delete from COLUNAS_TAB_INTERFACE where TABLE_NAME = 'BG_CONDICAO_PAGAMENTO_IDIOMA';

begin

  update	TAB_INTERFACE             
  set		DESCRICAO = '',
		CTL_NAME = 'BG_CONDICAO_PAGAMENTO_IDIOMA', 
		PREFIX_FILE = 'COPGID', 
		GERAR_CTL = 'S', 
		PRIORIDADE = 0, 
		ODBC_SOURCE_NAME = '', 
		ODBC_USER = '', 
		ODBC_PASSWORD = '', 
		ODBC_TABLE_NAME = '', 
		ODBC_WHERE = '', 
		ODBC_SELECT_ESPECIFICO = '', 
		TIPO_INTERFACE = 'LOADER',
		ORACLE_INITIAL_EXTENT = '', 
		ORACLE_NEXT_EXTENT = '', 
		ORACLE_INDEX_TABLESPACE = '',
		ELIMINAR_REG_EXECUCAO = 'N',
		ID_SISTEMA = 'BG',
		PROCEDURE_NAME = 'PKG_SFW_COND_PAGAMENTO.PRC_COND_PAGTO_IDIOMA',
		SEPARADOR = '',
		CTL_FIXO = null,
		TRIGGER1 = null,
		TRIGGER2 = null,
		COMANDO_EXTRA_LOADER = ''
  where TABLE_NAME = 'BG_CONDICAO_PAGAMENTO_IDIOMA';

  if SQL%notfound then
		insert into TAB_INTERFACE 
		(TABLE_NAME,
		DESCRICAO, 
		CTL_NAME, 
		PREFIX_FILE, 
		GERAR_CTL, 
		PRIORIDADE, 
		ODBC_SOURCE_NAME, 
		ODBC_USER, 
		ODBC_PASSWORD, 
		ODBC_TABLE_NAME, 
		ODBC_WHERE, 
		ODBC_SELECT_ESPECIFICO, 
		TIPO_INTERFACE, 
		ORACLE_INITIAL_EXTENT, 
		ORACLE_NEXT_EXTENT, 
		ORACLE_INDEX_TABLESPACE, 
		ELIMINAR_REG_EXECUCAO, 
		COMANDO_EXTRA_LOADER, 
		ID_SISTEMA, 
		PROCEDURE_NAME, 
		SEPARADOR)
		values
		('BG_CONDICAO_PAGAMENTO_IDIOMA',
		'',
		'BG_CONDICAO_PAGAMENTO_IDIOMA', 
		'COPGID', 
		'S', 
		0, 
		'', 
		'', 
		'', 
		'', 
		'', 
		'', 
		'LOADER', 
		'', 
		'', 
		'',
		'N',
		'',
		'BG',
		'PKG_SFW_COND_PAGAMENTO.PRC_COND_PAGTO_IDIOMA',
		'');
  end if;
end;
/


insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_CONDICAO_PAGAMENTO_IDIOMA', 'COD_COND_PAGTO', 'CHAR', '', '', '20', 
'1','Código da Condição de Pagamento','PI_COD_COND_PAGTO','');

insert into DEPEND_HEADER_ITEM
(	TABLE_NAME,
	COLUMN_NAME,
	TABLE_NAME_HEADER,
	COLUMN_NAME_HEADER, 
	OBRIG)
values	
(	'BG_CONDICAO_PAGAMENTO_IDIOMA' ,
	'COD_COND_PAGTO' ,
	'BG_CONDICAO_PAGAMENTO' ,
	'COD_COND_PAGTO',
	'N' );

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_CONDICAO_PAGAMENTO_IDIOMA', 'COD_SISTEMA', 'CHAR', '', '', '10', 
'2','Código do Sistema','PI_COD_SISTEMA','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_CONDICAO_PAGAMENTO_IDIOMA', 'COD_IDIOMA', 'CHAR', '', '', '20', 
'3','Código do Idioma','PI_COD_IDIOMA','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_CONDICAO_PAGAMENTO_IDIOMA', 'TRADUCAO_COND_PAGTO', 'CHAR', '', '', '150', 
'4','Descrição da Condição de Pagamento no Idioma','PI_TRADUCAO_COND_PAGTO','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_CONDICAO_PAGAMENTO_IDIOMA', 'TIPO_TRANSACAO', 'CHAR', '', '', '1', 
'5','','PI_TIPO_TRANSACAO','');

commit;

set serveroutput on
exec PRC_SINCRONIZA_TABELA('BG_CONDICAO_PAGAMENTO_IDIOMA');

CREATE OR REPLACE
TRIGGER TI_BG_CONDICAO_PAGAMENTO_IDIOM
BEFORE INSERT ON BG_CONDICAO_PAGAMENTO_IDIOMA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	select seq_id_interface.nextval into :new.id from dual;

  begin
    select id
    into :new.id_ref
    from BG_CONDICAO_PAGAMENTO
    where id_importacao = :new.id_importacao
    and COD_COND_PAGTO = :new.COD_COND_PAGTO;
  exception
    when no_data_found then
      :new.id_ref := null;   
    when others then
      raise_application_error(-20001,'Falha em busca de header: ' || sqlerrm(sqlcode()));
  end;
 
end;
/


CREATE OR REPLACE
TRIGGER TIA_BG_CONDICAO_PAGAMENTO_IDIO
AFTER INSERT ON BG_CONDICAO_PAGAMENTO_IDIOMA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	insert into REGISTROS_INTERFACES (ID, ID_IMPORTACAO, TABLE_NAME, ID_REF)
	values (:new.id, :new.id_importacao, 'BG_CONDICAO_PAGAMENTO_IDIOMA', :new.id_ref);
end;
/


CREATE OR REPLACE
TRIGGER TD_BG_CONDICAO_PAGAMENTO_IDIOM
BEFORE DELETE ON BG_CONDICAO_PAGAMENTO_IDIOMA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	delete from REGISTROS_INTERFACES where id = :old.id;
end;
/


 
-- //////
-- //////  Não esqueça de reprocessar todos os GRANTS para todos os sistemas
-- //////

