package com.polyarniq.calkgp.ui.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.polyarniq.calkgp.R
import com.polyarniq.calkgp.databinding.FragmentResultBinding
import com.polyarniq.calkgp.util.FormatUtils

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amount = arguments?.getDouble("amount") ?: 0.0
        val legalRef = arguments?.getString("legalRef") ?: ""
        val description = arguments?.getString("description") ?: ""

        binding.textAmount.text = FormatUtils.formatCurrencyInt(amount)
        binding.textDescription.text = description
        binding.textLegalRef.text = legalRef

        binding.btnAgain.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.btnCopy.setOnClickListener {
            val text = "Госпошлина: ${FormatUtils.formatCurrencyInt(amount)}\n$description\n$legalRef"
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("gov_duty", text))
            Toast.makeText(requireContext(), getString(R.string.copied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
