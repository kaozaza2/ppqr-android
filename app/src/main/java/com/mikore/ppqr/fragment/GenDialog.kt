/*
 * Copyright (C) 2021 Software with Kao.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mikore.ppqr.fragment

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

import com.mikore.ppqr.R
import com.mikore.ppqr.database.AccountType
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.database.AppHistory

class GenDialog : DialogFragment() {

    private lateinit var genBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var accountNo: EditText
    private lateinit var amount: EditText
    private lateinit var desc: EditText

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_quick_gen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.color.lightTransparent)
        setStyle(STYLE_NO_INPUT, android.R.style.Theme)

        genBtn = view.findViewById(R.id.quick_btn_gen)
        cancelBtn = view.findViewById(R.id.quick_btn_cancel)
        accountNo = view.findViewById(R.id.quick_no_edit)
        amount = view.findViewById(R.id.quick_amount_edit)
        desc = view.findViewById(R.id.quick_desc_edit)

        cancelBtn.setOnClickListener {
            dismiss()
        }

        genBtn.setOnClickListener {
            val no = accountNo.text.toString()
            val am = amount.text.toString()
            var ds = desc.text.toString()
            if (no.isEmpty() || no.length < 10 || no.length > 15) {
                Toast.makeText(
                    requireContext(),
                    "Please enter valid account no.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                QRPopup(
                    AppHistory( "", ds.ifEmpty { "Quick Generate" }, am.ifEmpty { null }),
                    AppAccount("", null, no, AccountType.fromLength(no.length))
                ).show(requireActivity().supportFragmentManager, "quick_gen")
            }
        }

    }
}